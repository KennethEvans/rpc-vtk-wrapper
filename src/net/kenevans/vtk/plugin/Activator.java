package net.kenevans.vtk.plugin;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.kenevans.vtk.preferences.VTKPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

  // The plug-in ID
  public static final String PLUGIN_ID = "org.vtk";
  public static final boolean debug = false;

  // The shared instance
  private static Activator plugin;

  /**
   * The constructor
   */
  public Activator() {
    if(debug) {
      System.out.println("VTKPlugin CTOR");
    }
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    // DEBUG
    if(debug) {
      System.out.println("org.vtk Activator.start");
      System.out.println("Thread: " + Thread.currentThread());
      System.out.println("ClassLoader: " + this.getClass().getClassLoader());

      ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
      System.out.println("SystemClassLoader: " + sysClassLoader);
      String[] propNames = {"java.home", "java.system.class.loader"};
      String name;
      for(int i = 0; i < propNames.length; i++) {
        name = System.getProperty(propNames[i], "<Not found>");
        System.out.println(propNames[i] + ": " + name);
      }
      if(true) {
        String path = System.getProperty("java.library.path");
        String split[] = path.split(";");
        String msg = "";
        if(path != null) {
          msg += "java.library.path: \n";
          for(int i = 0; i < split.length; i++) {
            msg += "  " + split[i] + "\n";
          }
        }
        System.out.println(msg);
      }
      if(false) {
        String mapName = System.mapLibraryName("vtkCommonJava");
        System.out.println("System.mapLibraryName(\"vtkCommonJava\"): "
          + mapName);
      }
    }

    // Call the super start method first, otherwise the preferences are not
    // available
    super.start(context);

    // Get the preferences
    IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
    String pathPref = null;
    if(prefs != null) {
      pathPref = prefs.getString(VTKPreferenceConstants.P_PATH);
    }
    String[] paths = {};
    String pathSep = File.pathSeparator;
    String sep = File.separator;
    if(pathPref != null) {
      paths = pathPref.trim().split(pathSep);
    }

    // VTK uses jawt, which is found in jre/bin but not jdk/bin. We will use a
    // different logic to find it than in finding the VTK libraries.
    String libName = "jawt";
    String fullLibName = null;
    boolean found = false;
    // First try the users path from preferences
    if(pathPref != null && pathPref.length() > 0) {
      // There is a preference, use load
      found = false;
      for(int j = 0; j < paths.length; j++) {
        try {
          fullLibName = paths[j] + sep + System.mapLibraryName(libName);
          System.load(fullLibName);
          found = true;
          break;
        } catch(Throwable t) {
          if(debug) {
            System.out.println("Tried " + fullLibName);
            System.out.println(t.getMessage());
          }
          continue;
        }
      }
    }
    // Next try loadLibrary
    if(!found) {
      // No preference, use loadLibrary
      fullLibName = "jawt";
      try {
        System.loadLibrary(fullLibName);
        found = true;
      } catch(Throwable t) {
        if(debug) {
          System.out.println("Tried " + fullLibName);
          System.out.println(t.getMessage());
        }
      }
    }
    // Next try manufacturing the path and use System.load().
    if(!found) {
      // At one time it seemed this was needed, but it doesn't seem to be
      // now.
      // Doesn't hurt to leave it in.
      try {
        String jre = System.getProperty("java.home");
        fullLibName = jre + sep + System.mapLibraryName(libName);
        System.load(fullLibName);
        found = true;
      } catch(Throwable t) {
        if(debug) {
          System.out.println("Tried " + fullLibName);
          System.out.println(t.getMessage());
        }
      }
    }
    if(!found) {
      final String msg = "Problems loading native jawt library needed "
        + "by VTK libraries\n"
        + "(Usually found in the jre/bin directory of your Java)\n";
      errMsg(msg);
      System.out.println(msg);
    }

    // We need to load all the libraries here, loading the dependent ones
    // before
    // the ones that depend on them, since the dependent ones will be using
    // the
    // OS to find them, not OSGI
    String[] libraries = new String[] {
      // No dependencies
      "vtksys", "vtkfreetype", "vtkzlib", "vtkjpeg", "vtkexpat",
      "vtkNetCDF",
      "vtkDICOMParser",
      "vtkverdict",
      // At least one dependency
      "vtkCommon", "vtkCommonJava", "vtkFiltering", "vtkFilteringJava",
      "vtkImaging", "vtkGraphics", "vtkftgl", "vtktiff", "vtkpng", "vtkpng",
      "vtkmetaio", "vtkIO", "vtkexoIIc",
      // Many dependencies
      "vtkRendering", "vtkRenderingJava", "vtkImagingJava", "vtkHybrid",
      "vtkHybridJava", "vtkVolumeRendering", "vtkVolumeRenderingJava",};

    try {
      for(int i = 0; i < libraries.length; i++) {
        libName = libraries[i];
        // Note that we look one place or the other, not both. This is so the
        // user can control what is used.
        if(pathPref == null || pathPref.length() == 0) {
          // No preference, use loadLibrary
          System.loadLibrary(libName);
        } else {
          // There is a preference, use load
          found = false;
          for(int j = 0; j < paths.length; j++) {
            try {
              fullLibName = paths[j] + sep + System.mapLibraryName(libName);
              System.load(fullLibName);
              found = true;
              break;
            } catch(Throwable t) {
              continue;
            }
          }
          if(!found) {
            String msg = "Problems loading VTK libraries\n" + "Cannot load "
              + libName + "\nusing the VTK Lookup Path in your Preferences:\n";
            for(int j = 0; j < paths.length; j++) {
              msg += " " + paths[j] + "\n";
            }
            errMsg(msg);
            break;
          }
        }
      }
    } catch(Throwable t) {
      String msg = "Problems loading VTK libraries\n" + t.getMessage();
      errMsg(msg);
      System.out.println(msg);
      t.printStackTrace();
    }

    // DEBUG Use this as a check as it is what vtkPanel does
    if(debug) {
      try {
        System.loadLibrary("vtkCommonJava");
        System.loadLibrary("vtkFilteringJava");
        System.loadLibrary("vtkIOJava");
        System.loadLibrary("vtkImagingJava");
        System.loadLibrary("vtkGraphicsJava");
        System.loadLibrary("vtkRenderingJava");
        System.loadLibrary("vtkHybridJava");
        System.loadLibrary("vtkVolumeRenderingJava");
      } catch(Throwable t) {
        String msg = "Problems loading VTK libraries that vtkPanel loads\n"
          + t.getMessage();
        System.out.println(msg);
        // t.printStackTrace();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  /**
   * Displays an error MessageDialog using asyncExec.
   * 
   * @param shell Can be null.
   * @param msg
   */
  public static void errMsg(final String msg) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
          msg);
      }
    });
  }
}
