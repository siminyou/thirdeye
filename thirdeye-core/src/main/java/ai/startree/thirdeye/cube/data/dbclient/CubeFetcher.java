/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.cube.data.dbclient;

import ai.startree.thirdeye.cube.data.dbrow.Dimensions;
import ai.startree.thirdeye.cube.data.dbrow.Row;
import com.google.common.collect.Multimap;
import java.util.List;

/**
 * The database client that provides the function of data retrieval for the cube algorithm.
 */
public interface CubeFetcher<R extends Row> {

  /**
   * Returns the baseline and current value for the root node.
   *
   * @param filterSets the data filter.
   * @return a row of data that contains the baseline and current value for the root node.
   */
  R getTopAggregatedValues(Multimap<String, String> filterSets) throws Exception;

  /**
   * Returns the baseline and current value for nodes at each dimension from the given list.
   * For instance, if the list has ["country", "page name"], then it returns nodes of ["US", "IN",
   * "JP", ...,
   * "linkedin.com", "google.com", ...]
   *
   * @param dimensions the list of dimensions.
   * @param filterSets the data filter.
   * @return the baseline and current value for nodes at each dimension from the given list.
   */
  List<List<R>> getAggregatedValuesOfDimension(Dimensions dimensions,
      Multimap<String, String> filterSets)
      throws Exception;

  /**
   * Returns the baseline and current value for nodes for each dimension combination.
   * For instance, if the list has ["country", "page name"], then it returns nodes of
   * [
   * ["US", "IN", "JP", ...,],
   * ["US, linkedin.com", "US, google.com", "IN, linkedin.com", "IN, google.com", "JP,
   * linkedin.com", "JP, google.com", ...]
   * ]
   *
   * @param dimensions the dimensions to be drilled down.
   * @param filterSets the data filter.
   * @return the baseline and current value for nodes for each dimension combination.
   */
  List<List<R>> getAggregatedValuesOfLevels(Dimensions dimensions,
      Multimap<String, String> filterSets)
      throws Exception;
}