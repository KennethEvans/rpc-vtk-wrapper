package net.kenevans.vtk.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import net.kenevans.vtk.plugin.Activator;

/**
 * Class used to initialize default preference values.
 */
public class VTKPreferenceInitializer extends AbstractPreferenceInitializer
{

  /*
   * (non-Javadoc)
   * 
   * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
   * initializeDefaultPreferences()
   */
  public void initializeDefaultPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(VTKPreferenceConstants.P_PATH, "");
  }

}
