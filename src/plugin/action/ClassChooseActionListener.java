package plugin.action;

import com.intellij.ide.util.ClassFilter;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassChooseActionListener implements ActionListener {

   private ReferenceEditorComboWithBrowseButton comboWithBrowseButton;

   private Project project;

   private String dialogName;

   public ClassChooseActionListener(
           ReferenceEditorComboWithBrowseButton comboWithBrowseButton,
           Project project,
           String dialogName) {
      this.comboWithBrowseButton = comboWithBrowseButton;
      this.project = project;
      this.dialogName = dialogName;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      TreeJavaClassChooserDialog chooser = new TreeJavaClassChooserDialog (
              dialogName,
              project,
              GlobalSearchScope.allScope(project),
              ClassFilter.ALL,
              null
      );
      chooser.show();
      PsiClass psiClass = chooser.getSelected();
      if (psiClass != null) {
         comboWithBrowseButton.setText(psiClass.getQualifiedName());
      }
   }
}
