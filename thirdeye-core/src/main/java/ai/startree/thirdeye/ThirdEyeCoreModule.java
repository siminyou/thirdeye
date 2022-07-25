/*
 * Copyright 2022 StarTree Inc
 *
 * Licensed under the StarTree Community License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.startree.ai/legal/startree-community-license
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT * WARRANTIES OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing permissions and limitations under
 * the License.
 */
package ai.startree.thirdeye;

import ai.startree.thirdeye.auth.AuthConfiguration;
import ai.startree.thirdeye.auth.OAuthConfiguration;
import ai.startree.thirdeye.config.ThirdEyeServerConfiguration;
import ai.startree.thirdeye.config.TimeConfiguration;
import ai.startree.thirdeye.config.UiConfiguration;
import ai.startree.thirdeye.datalayer.ThirdEyePersistenceModule;
import ai.startree.thirdeye.datasource.AutoOnboardConfiguration;
import ai.startree.thirdeye.datasource.loader.DefaultAggregationLoader;
import ai.startree.thirdeye.detection.DefaultDataProvider;
import ai.startree.thirdeye.detection.anomaly.monitor.MonitorConfiguration;
import ai.startree.thirdeye.detection.cache.CacheConfig;
import ai.startree.thirdeye.events.HolidayEventsLoaderConfiguration;
import ai.startree.thirdeye.notification.NotificationConfiguration;
import ai.startree.thirdeye.rootcause.configuration.RcaConfiguration;
import ai.startree.thirdeye.scheduler.ThirdEyeSchedulerConfiguration;
import ai.startree.thirdeye.spi.datasource.loader.AggregationLoader;
import ai.startree.thirdeye.spi.detection.DataProvider;
import ai.startree.thirdeye.task.TaskDriverConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import org.apache.tomcat.jdbc.pool.DataSource;

public class ThirdEyeCoreModule extends AbstractModule {

  private final DataSource dataSource;
  private final ThirdEyeServerConfiguration configuration;

  public ThirdEyeCoreModule(final DataSource dataSource,
      final ThirdEyeServerConfiguration configuration) {
    this.dataSource = dataSource;
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    install(new ThirdEyePersistenceModule(dataSource));

    bind(DataProvider.class).to(DefaultDataProvider.class).in(Scopes.SINGLETON);
    bind(AggregationLoader.class).to(DefaultAggregationLoader.class).in(Scopes.SINGLETON);

    bind(CacheConfig.class)
        .toProvider(configuration::getCacheConfig)
        .in(Scopes.SINGLETON);

    bind(RcaConfiguration.class)
        .toProvider(configuration::getRcaConfiguration)
        .in(Scopes.SINGLETON);

    bind(ThirdEyeSchedulerConfiguration.class)
        .toProvider(configuration::getSchedulerConfiguration)
        .in(Scopes.SINGLETON);

    bind(TaskDriverConfiguration.class)
        .toProvider(configuration::getTaskDriverConfiguration)
        .in(Scopes.SINGLETON);

    bind(UiConfiguration.class)
        .toProvider(configuration::getUiConfiguration)
        .in(Scopes.SINGLETON);

    bind(AuthConfiguration.class)
        .toProvider(configuration::getAuthConfiguration)
        .in(Scopes.SINGLETON);

    bind(NotificationConfiguration.class)
        .toProvider(configuration::getNotificationConfiguration)
        .in(Scopes.SINGLETON);

    bind(TimeConfiguration.class)
        .toProvider(configuration::getTimeConfiguration)
        .in(Scopes.SINGLETON);
  }

  @Singleton
  @Provides
  public HolidayEventsLoaderConfiguration getHolidayEventsLoaderConfiguration(
      ThirdEyeSchedulerConfiguration configuration) {
    return configuration.getHolidayEventsLoaderConfiguration();
  }

  @Singleton
  @Provides
  public AutoOnboardConfiguration getAutoOnboardConfiguration(
      ThirdEyeSchedulerConfiguration configuration) {
    return configuration.getAutoOnboardConfiguration();
  }

  @Singleton
  @Provides
  public MonitorConfiguration getMonitorConfiguration(
      ThirdEyeSchedulerConfiguration schedulerConfiguration) {
    return schedulerConfiguration.getMonitorConfiguration();
  }

  @Singleton
  @Provides
  public OAuthConfiguration getOAuthConfig(
      AuthConfiguration authConfiguration) {
    return authConfiguration.getOAuthConfig();
  }
}
