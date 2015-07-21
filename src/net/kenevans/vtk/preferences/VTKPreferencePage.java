package net.kenevans.vtk.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.kenevans.vtk.plugin.Activator;

/**
 * Preference page for VTK.
 */
public class VTKPreferencePage extends FieldEditorPreferencePage implements
  IWorkbenchPreferencePage
{

  /**
   * PreferencePage constructor.
   */
  public VTKPreferencePage() {
    super(GRID);
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("VTK Preferences");
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI
   * blocks needed to manipulate various types of preferences. Each field editor
   * knows how to save and restore itself.
   */
  public void createFieldEditors() {
    Composite parent = getFieldEditorParent();
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    parent.setLayout(layout);

    addField(new StringFieldEditor(VTKPreferenceConstants.P_PATH,
      "VTK Lookup Path:", getFieldEditorParent()));

    // Blank line
    Label label = new Label(parent, SWT.NONE);
    label.setText("");
    GridDataFactory.fillDefaults().span(2, 0).applyTo(label);
    // Comment
    label = new Label(parent, SWT.NONE);
    label
      .setText("Specify a path specifying where to look for VTK native "
        + "libraries.\n"
        + "Use the os path delimiter to separate directories in the path.\n\n"
        + "This should not be necessary if the libraries are in your os PATH\n"
        + " or if they are found in the VTK plug-in.  If this path is specified,\n"
        + "the ones in the VTK plug-in or the os PATH will not be used.\n\n"
        + "You will need to resart the workspace for the change to take effect.");
    GridDataFactory.fillDefaults().grab(false, false).span(2, 1).applyTo(label);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench) {
  }

}