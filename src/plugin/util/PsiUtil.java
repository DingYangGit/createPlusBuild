package plugin.util;

import com.intellij.psi.*;

public class PsiUtil {

    //todo can add match on import ...*
    public static void addImport(PsiImportList psiImportList, PsiImportStatement psiImportStatement) {
        if (psiImportList == null) {
            return;
        }
        PsiImportStatement[] psiImportStatements = psiImportList.getImportStatements();
        for (PsiImportStatement statement: psiImportStatements) {
            if (statement.getQualifiedName() != null &&
                    statement.getQualifiedName().equals(psiImportStatement.getQualifiedName())) {
                return;
            }
        }
        psiImportList.add(psiImportStatement);
    }

}
