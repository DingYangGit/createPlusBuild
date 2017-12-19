package plugin;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import org.bouncycastle.util.Strings;
import plugin.util.PsiUtil;
import plugin.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;

public class TextGenerator {

    private final PsiClass ownerClass;
    private final PsiClass sourceClass;
    private final PsiClass destClass;
    private final String methodName;
    private final PsiElementFactory elementFactory;
    public TextGenerator(PsiClass ownerClass,
                         PsiClass sourceClass,
                         PsiClass destClass,
                         String methodName) {
        this.ownerClass = ownerClass;
        this.sourceClass = sourceClass;
        this.destClass = destClass;
        this.methodName = methodName;
        elementFactory = getElementFactory(ownerClass.getProject());
    }

    public void generate() {
        new WriteCommandAction.Simple(ownerClass.getProject(), ownerClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                generateMethod();
                generateImport();
            }
        }.execute();
    }

    private void generateImport() {
        PsiImportList psiImportList = ((PsiJavaFile) (ownerClass.getContainingFile()))
                .getImportList();
        PsiUtil.addImport(psiImportList, elementFactory.createImportStatement(sourceClass));
        PsiUtil.addImport(psiImportList, elementFactory.createImportStatement(destClass));
    }

    private void generateMethod() {
        PsiMethod method = createMethod(generateMethodText());
        ownerClass.add(method);
    }

    private String generateMethodText() {
        List<PsiMethod> psiMethodsDest =
                Arrays.stream(destClass.getAllMethods()).collect(Collectors.toList());
        List<PsiMethod> psiMethodsSource =
                Arrays.stream(sourceClass.getAllMethods()).collect(Collectors.toList());
        Map<String, PsiMethod> psiMethodsDestSet =
                psiMethodsDest.stream().filter(psiMethod ->
                        !psiMethod.isConstructor()
                                && ("set".equals(psiMethod.getName().substring(0, 3))
                                && psiMethod.getParameterList().getParametersCount() == 1)
                ).collect(Collectors.toMap(PsiMethod::getName, psiMethod -> psiMethod));
        Map<String, PsiMethod> psiMethodsSourceGet =
                psiMethodsSource.stream().filter(psiMethod ->
                        !psiMethod.isConstructor()
                                && ("get".equals(psiMethod.getName().substring(0, 3))
                                && psiMethod.getParameterList().getParametersCount() == 0)
                ).collect(Collectors.toMap(PsiMethod::getName, psiMethod -> psiMethod));
        StringBuilder methodBody = new StringBuilder("");
        methodBody.append(destClass.getName())
                .append(" ")
                .append(StringUtil.toLowerStanding(destClass.getName()))
                .append(" = ")
                .append("new ")
                .append(destClass.getName())
                .append("();\n");
        psiMethodsDestSet.forEach((methodNameSet, psiMethodSet) -> {
                    PsiMethod matchedPsiMethodGet = psiMethodsSourceGet
                            .get("get" + methodNameSet.substring(3));
                    if (matchedPsiMethodGet != null
                            && psiMethodSet.getParameterList().getParameters()[0].getType()
                            .equals(matchedPsiMethodGet.getReturnType())) {
                        methodBody.append(StringUtil.toLowerStanding(destClass.getName()))
                                .append(".")
                                .append(methodNameSet)
                                .append("(")
                                .append(StringUtil.toLowerStanding(sourceClass.getName()))
                                .append(".")
                                .append(matchedPsiMethodGet.getName())
                                .append("());\n");
                        return;
                    }
                    for (Map.Entry<String, PsiMethod> entry: psiMethodsSourceGet.entrySet()) {
                        String methodNameGet = entry.getKey();
                        PsiMethod psiMethodGet = entry.getValue();
                        if (methodNameSet.substring(3).equalsIgnoreCase(methodNameGet.substring(3))
                                && psiMethodSet.getParameterList().getParameters()[0].getType()
                                .equals(psiMethodGet.getReturnType())) {
                            methodBody.append(StringUtil.toLowerStanding(destClass.getName()))
                                    .append(".")
                                    .append(methodNameSet)
                                    .append("(")
                                    .append(StringUtil.toLowerStanding(sourceClass.getName()))
                                    .append(".")
                                    .append(psiMethodGet.getName())
                                    .append("());\n");
                            return;
                        }
                    }
                    methodBody.append(StringUtil.toLowerStanding(destClass.getName()))
                            .append(".")
                            .append(methodNameSet)
                            .append("(...);\n");
                }
        );
        methodBody.append("return ")
                .append(Character.toLowerCase(destClass.getName().charAt(0)))
                .append(destClass.getName().substring(1))
                .append(";\n");
        return new StringBuilder("public ")
                .append(this.destClass.getName())
                .append(" ")
                .append(methodName)
                .append("(")
                .append(sourceClass.getName())
                .append(" ")
                .append(StringUtil.toLowerStanding(sourceClass.getName()))
                .append(")")
                .append(" {\n")
                .append(methodBody)
                .append("}").toString();
    }



    private PsiMethod createMethod(String methodText) {
        return elementFactory.createMethodFromText(methodText, destClass);
    }
}
