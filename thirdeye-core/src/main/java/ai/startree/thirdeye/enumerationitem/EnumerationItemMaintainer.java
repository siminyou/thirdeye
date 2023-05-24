/*
 * Copyright 2023 StarTree Inc
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

package ai.startree.thirdeye.enumerationitem;

import static java.util.stream.Collectors.toList;

import ai.startree.thirdeye.spi.datalayer.bao.EnumerationItemManager;
import ai.startree.thirdeye.spi.datalayer.dto.AlertDTO;
import ai.startree.thirdeye.spi.datalayer.dto.EnumerationItemDTO;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;

@Singleton
public class EnumerationItemMaintainer {

  private final EnumerationItemManager enumerationItemManager;

  @Inject
  public EnumerationItemMaintainer(final EnumerationItemManager enumerationItemManager) {
    this.enumerationItemManager = enumerationItemManager;
  }

  public static AlertDTO alertRef(final Long alertId) {
    final AlertDTO alert = new AlertDTO();
    alert.setId(alertId);
    return alert;
  }

  public List<EnumerationItemDTO> sync(
      final List<EnumerationItemDTO> enumerationItems, final List<String> idKeys,
      final Long alertId) {
    return enumerationItems.stream()
        .map(source -> source.setAlert(alertRef(alertId)))
        .map(source -> enumerationItemManager.findExistingOrCreate(source, idKeys))
        .collect(toList());
  }
}
