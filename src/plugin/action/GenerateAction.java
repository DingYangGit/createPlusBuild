package plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import plugin.dialog.GenerateDialog;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;

public class GenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        event.getDataContext();
        PsiClass psiClass = getPsiClass(event);
        GenerateDialog dialog = new GenerateDialog(
                psiClass,
                event.getPresentation().getText());
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClass(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    private PsiClass getPsiClass(AnActionEvent e) {
        PsiFile psiFile = e.getData(PSI_FILE);
        Editor editor = e.getData(EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return getParentOfType(elementAt, PsiClass.class);
    }

}
