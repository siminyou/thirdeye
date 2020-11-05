/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.pinot.thirdeye.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.pinot.thirdeye.common.time.TimeGranularity;
import org.apache.pinot.thirdeye.common.time.TimeSpec;
import org.apache.pinot.thirdeye.constant.MetricAggFunction;
import org.apache.pinot.thirdeye.datalayer.dto.DatasetConfigDTO;
import org.apache.pinot.thirdeye.datasource.DAORegistry;
import org.apache.pinot.thirdeye.datasource.MetricExpression;
import org.apache.pinot.thirdeye.datasource.MetricFunction;
import org.apache.pinot.thirdeye.datasource.ThirdEyeCacheRegistry;
import org.apache.pinot.thirdeye.util.ThirdEyeUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

  private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static List<String> getSortedDimensionNames(String collection)
      throws Exception {
    List<String> dimensions = new ArrayList<>(getSchemaDimensionNames(collection));
    Collections.sort(dimensions);
    return dimensions;
  }

  public static List<String> getSchemaDimensionNames(String collection) throws Exception {
    DatasetConfigDTO datasetConfig = ThirdEyeCacheRegistry.getInstance().getDatasetConfigCache()
        .get(collection);
    return datasetConfig.getDimensions();
  }

  public static List<String> getDimensionsToGroupBy(String collection,
      Multimap<String, String> filters)
      throws Exception {
    List<String> dimensions = Utils.getSortedDimensionNames(collection);

    List<String> dimensionsToGroupBy = new ArrayList<>();
    if (filters != null) {
      Set<String> filterDimenions = filters.keySet();
      for (String dimension : dimensions) {
        if (!filterDimenions.contains(dimension)) {
          // dimensions.remove(dimension);
          dimensionsToGroupBy.add(dimension);
        }
      }
    } else {
      return dimensions;
    }
    return dimensionsToGroupBy;
  }

  public static List<MetricExpression> convertToMetricExpressions(String metricsJson,
      MetricAggFunction aggFunction, String dataset) throws ExecutionException {

    List<MetricExpression> metricExpressions = new ArrayList<>();
    if (metricsJson == null) {
      return metricExpressions;
    }
    ArrayList<String> metricExpressionNames;
    try {
      TypeReference<ArrayList<String>> valueTypeRef = new TypeReference<ArrayList<String>>() {
      };
      metricExpressionNames = OBJECT_MAPPER.readValue(metricsJson, valueTypeRef);
    } catch (Exception e) {
      metricExpressionNames = new ArrayList<>();
      String[] metrics = metricsJson.split(",");
      for (String metric : metrics) {
        metricExpressionNames.add(metric.trim());
      }
    }
    for (String metricExpressionName : metricExpressionNames) {
      String derivedMetricExpression = ThirdEyeUtils
          .getDerivedMetricExpression(metricExpressionName, dataset);
      MetricExpression metricExpression = new MetricExpression(metricExpressionName,
          derivedMetricExpression,
          aggFunction, dataset);
      metricExpressions.add(metricExpression);
    }
    return metricExpressions;
  }

  public static List<MetricFunction> computeMetricFunctionsFromExpressions(
      List<MetricExpression> metricExpressions) {
    Set<MetricFunction> metricFunctions = new HashSet<>();

    for (MetricExpression expression : metricExpressions) {
      metricFunctions.addAll(expression.computeMetricFunctions());
    }
    return Lists.newArrayList(metricFunctions);
  }

  /**
   * If the dataset is non-additive, then the bucket granularity is return. Otherwise, a
   * TimeGranularity that is
   * constructed from the given string of aggregation granularity is returned.
   *
   * @param aggTimeGranularity the string of aggregation granularity.
   * @param dataset the name of the dataset.
   * @return the available aggregation granularity for the given dataset.
   */
  public static TimeGranularity getAggregationTimeGranularity(String aggTimeGranularity,
      String dataset) {
    DatasetConfigDTO datasetConfig;
    try {
      datasetConfig = ThirdEyeCacheRegistry.getInstance().getDatasetConfigCache().get(dataset);
    } catch (ExecutionException e) {
      LOG.info(
          "Unable to determine whether dataset: {} is additive, the given aggregation granularity: {} is used.",
          dataset, aggTimeGranularity);
      return TimeGranularity.fromString(aggTimeGranularity);
    }

    if (datasetConfig.isAdditive()) {
      return TimeGranularity.fromString(aggTimeGranularity);
    } else {
      return datasetConfig.bucketTimeGranularity();
    }
  }

  /**
   * Given a duration (in millis), a time granularity, and the target number of chunk to divide the
   * duration, this method returns the time granularity that is able to divide the duration to a
   * number of chunks that is fewer than or equals to the target number.
   *
   * For example, if the duration is 25 hours, time granularity is HOURS, and target number is 12,
   * then the resized time granularity is 3_HOURS, which divide the duration to 9 chunks.
   *
   * @param duration the duration in milliseconds.
   * @param timeGranularityString time granularity in String format.
   * @param targetChunkNum the target number of chunks.
   * @return the resized time granularity in order to divide the duration to the number of chunks
   *     that is smaller than or equals to the target chunk number.
   */
  public static String resizeTimeGranularity(long duration, String timeGranularityString,
      int targetChunkNum) {
    TimeGranularity timeGranularity = TimeGranularity.fromString(timeGranularityString);

    long timeGranularityMillis = timeGranularity.toMillis();
    long chunkNum = duration / timeGranularityMillis;
    if (duration % timeGranularityMillis != 0) {
      ++chunkNum;
    }
    if (chunkNum > targetChunkNum) {
      long targetIntervalDuration = (long) Math.ceil((double) duration / (double) targetChunkNum);
      long unitTimeGranularityMillis = timeGranularity.getUnit().toMillis(1);
      int size = (int) Math
          .ceil((double) targetIntervalDuration / (double) unitTimeGranularityMillis);
      String newTimeGranularityString = size + "_" + timeGranularity.getUnit();
      return newTimeGranularityString;
    } else {
      return timeGranularityString;
    }
  }

  public static List<MetricExpression> convertToMetricExpressions(
      List<MetricFunction> metricFunctions) {
    List<MetricExpression> metricExpressions = new ArrayList<>();
    for (MetricFunction function : metricFunctions) {
      metricExpressions.add(new MetricExpression(function.getMetricName(), function.getDataset()));
    }
    return metricExpressions;
  }

  /*
   * This method returns the time zone of the data in this collection
   */
  public static DateTimeZone getDataTimeZone(String collection) {
    String timezone = TimeSpec.DEFAULT_TIMEZONE;
    try {
      DatasetConfigDTO datasetConfig;
      LoadingCache<String, DatasetConfigDTO> datasetConfigCache = ThirdEyeCacheRegistry
          .getInstance()
          .getDatasetConfigCache();
      if (datasetConfigCache != null && datasetConfigCache.get(collection) != null) {
        datasetConfig = ThirdEyeCacheRegistry.getInstance().getDatasetConfigCache().get(collection);
      } else {
        datasetConfig = DAORegistry.getInstance().getDatasetConfigDAO().findByDataset(collection);
      }
      if (datasetConfig != null) {
        timezone = datasetConfig.getTimezone();
      }
    } catch (ExecutionException e) {
      LOG.error("Exception while getting dataset config for {}", collection);
    }
    return DateTimeZone.forID(timezone);
  }

  public static String getJsonFromObject(Object obj) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(obj);
  }

  public static Map<String, Object> getMapFromJson(String json) throws IOException {
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
    };
    return OBJECT_MAPPER.readValue(json, typeRef);
  }

  public static Map<String, Object> getMapFromObject(Object object) throws IOException {
    return getMapFromJson(getJsonFromObject(object));
  }

  public static <T extends Object> List<T> sublist(List<T> input, int startIndex, int length) {
    startIndex = Math.min(startIndex, input.size());
    int endIndex = Math.min(startIndex + length, input.size());
    List<T> subList = Lists.newArrayList(input).subList(startIndex, endIndex);
    return subList;
  }

  public static long getMaxDataTimeForDataset(String dataset) {
    long endTime = 0;
    try {
      endTime = ThirdEyeCacheRegistry.getInstance().getDatasetMaxDataTimeCache().get(dataset);
    } catch (ExecutionException e) {
      LOG.error("Exception when getting max data time for {}", dataset);
    }
    return endTime;
  }
}
