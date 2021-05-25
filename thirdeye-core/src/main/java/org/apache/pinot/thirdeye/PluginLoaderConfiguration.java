package org.apache.pinot.thirdeye;

public class PluginLoaderConfiguration {

  private String pluginsPath = "plugins";

  public String getPluginsPath() {
    return pluginsPath;
  }

  public PluginLoaderConfiguration setPluginsPath(final String pluginsPath) {
    this.pluginsPath = pluginsPath;
    return this;
  }
}
