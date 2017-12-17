package plugin.dialog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import org.jetbrains.annotations.NotNull;
import plugin.TextGenerator;
import plugin.action.ClassChooseActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


public class GenerateDialog extends DialogWrapper {
    private static final int WIDTH = 40;
    private final PsiClass ownerClass;
    private ReferenceEditorComboWithBrowseButton referenceEditorComboWithBrowseButtonDest;
    private ReferenceEditorComboWithBrowseButton referenceEditorComboWithBrowseButtonSource;
    private String methodName;
    public GenerateDialog(PsiClass ownerClass, String methodName) {
        super(ownerClass.getProject(), true);
        this.methodName = methodName;
        this.ownerClass = ownerClass;
        referenceEditorComboWithBrowseButtonDest =
                new ReferenceEditorComboWithBrowseButton(
                        null, "", ownerClass.getProject(), true, "buttonDest");
        referenceEditorComboWithBrowseButtonSource =
                new ReferenceEditorComboWithBrowseButton(
                        null, "", ownerClass.getProject(), true, "buttonSource");
        referenceEditorComboWithBrowseButtonSource.addActionListener(
                new ClassChooseActionListener(
                        referenceEditorComboWithBrowseButtonSource,
                        ownerClass.getProject(),
                        "sourceChooser"
                        ));
        referenceEditorComboWithBrowseButtonDest.addActionListener(
                new ClassChooseActionListener(
                        referenceEditorComboWithBrowseButtonDest,
                        ownerClass.getProject(),
                        "destChooser"));
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    public void show() {
        super.init();
        getOKAction().setEnabled(false);
        super.show();
    }

    @Override
    protected void doOKAction() {
        String sourceClassName = referenceEditorComboWithBrowseButtonSource.getText();
        String destClassName = referenceEditorComboWithBrowseButtonDest.getText();
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(ownerClass.getProject());
        PsiClass psiClassSource = JavaPsiFacade.getInstance(ownerClass.getProject()).findClass(sourceClassName,
                searchScope);
        PsiClass psiClassDest = JavaPsiFacade.getInstance(ownerClass.getProject()).findClass(destClassName,
                searchScope);
        if (psiClassSource == null || psiClassDest == null) {
            return;
        }
        TextGenerator textGenerator = new TextGenerator(ownerClass,
                psiClassSource,
                psiClassDest,
                methodName);
        textGenerator.generate();
        super.doOKAction();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return referenceEditorComboWithBrowseButtonSource;
    }

    private Boolean identifyClassName(String className) {
        return JavaPsiFacade.getInstance(ownerClass.getProject()).findClass(className, getGlobalSearchScope()) != null;
    }

    private Boolean canShowOk() {
        return StringUtil.isNotEmpty(referenceEditorComboWithBrowseButtonSource.getText())
                && StringUtil.isNotEmpty(referenceEditorComboWithBrowseButtonDest.getText())
                && identifyClassName(referenceEditorComboWithBrowseButtonSource.getText())
                && identifyClassName(referenceEditorComboWithBrowseButtonDest.getText());
    }

    private GlobalSearchScope getGlobalSearchScope() {
        return GlobalSearchScope.allScope(ownerClass.getProject());
    }

    @Override
    protected JComponent createCenterPanel() {
        GlobalSearchScope searchScope = getGlobalSearchScope();
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(ownerClass.getProject()).getClassesByName(
                "GenerateAction", searchScope);
        psiClasses.getClass().getMethods();
        JavaPsiFacade.getInstance(ownerClass.getProject()).findClass("", searchScope);
        if (psiClasses.length > 0) {
            setTitle("input Class to " + methodName);
        } else {
            setTitle("input Class to " + methodName);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(IdeBorderFactory.createBorder());
        JPanel innerPanelSource = new JPanel(new BorderLayout());
        referenceEditorComboWithBrowseButtonSource.getChildComponent().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(com.intellij.openapi.editor.event.DocumentEvent event) {
                if (canShowOk()) {
                    getOKAction().setEnabled(true);
                }
            }
        });
        AnAction clickActionSource = new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                referenceEditorComboWithBrowseButtonSource.getButton().doClick();
            }
        };
        clickActionSource.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK)),
                referenceEditorComboWithBrowseButtonSource.getChildComponent());
        innerPanelSource.add(referenceEditorComboWithBrowseButtonSource, BorderLayout.CENTER);
        JPanel innerPanelDest = new JPanel(new BorderLayout());
        AnAction clickActionDest = new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                referenceEditorComboWithBrowseButtonDest.getButton().doClick();
            }
        };
        clickActionDest.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK)),
                referenceEditorComboWithBrowseButtonDest.getChildComponent());
        referenceEditorComboWithBrowseButtonDest.getChildComponent().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(com.intellij.openapi.editor.event.DocumentEvent event) {
                if (canShowOk()) {
                    getOKAction().setEnabled(true);
                }
            }
        });
        innerPanelDest.add(referenceEditorComboWithBrowseButtonDest, BorderLayout.CENTER);
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 2;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Source Class:"), gbConstraints);
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 2;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(innerPanelSource, gbConstraints);
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 3;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Target Class:"), gbConstraints);
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 3;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        Dimension size = innerPanelDest.getPreferredSize();
        FontMetrics fontMetrics = innerPanelDest.getFontMetrics(innerPanelDest.getFont());
        size.width = fontMetrics.charWidth('a') * WIDTH;
        innerPanelDest.setPreferredSize(size);
        panel.add(innerPanelDest, gbConstraints);
        return panel;
    }


}
