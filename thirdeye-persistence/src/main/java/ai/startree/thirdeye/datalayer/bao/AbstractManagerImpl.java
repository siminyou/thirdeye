/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.datalayer.bao;

import ai.startree.thirdeye.datalayer.dao.GenericPojoDao;
import ai.startree.thirdeye.spi.datalayer.DaoFilter;
import ai.startree.thirdeye.spi.datalayer.Predicate;
import ai.startree.thirdeye.spi.datalayer.bao.AbstractManager;
import ai.startree.thirdeye.spi.datalayer.dto.AbstractDTO;
import com.google.inject.persist.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

public abstract class AbstractManagerImpl<E extends AbstractDTO> implements AbstractManager<E> {

  protected final GenericPojoDao genericPojoDao;
  private final Class<? extends AbstractDTO> dtoClass;

  protected AbstractManagerImpl(final Class<? extends AbstractDTO> dtoClass,
      final GenericPojoDao genericPojoDao) {
    this.dtoClass = dtoClass;
    this.genericPojoDao = genericPojoDao;
  }

  @Override
  public Long save(final E entity) {
    if (entity.getId() != null) {
      //TODO: throw exception and force the caller to call update instead
      update(entity);
      return entity.getId();
    }
    final Long id = genericPojoDao.put(entity);
    entity.setId(id);
    return id;
  }

  @Override
  public int update(final E entity, final Predicate predicate) {
    return genericPojoDao.update(entity, predicate);
  }

  @Override
  public int update(final E entity) {
    return genericPojoDao.update(entity);
  }

  // Test is located at TestAlertConfigManager.testBatchUpdate()
  @Override
  public int update(List<E> entities) {
    return genericPojoDao.update(entities);
  }

  @Override
  public E findById(final Long id) {
    return (E) genericPojoDao.get(id, dtoClass);
  }

  @Override
  public List<E> findByIds(final List<Long> ids) {
    return (List<E>) genericPojoDao.get(ids, dtoClass);
  }

  @Override
  public List<E> findByName(String name) {
    return findByPredicate(Predicate.EQ("name", name));
  }

  @Override
  public int delete(final E entity) {
    return genericPojoDao.delete(entity.getId(), dtoClass);
  }

  // Test is located at TestAlertConfigManager.testBatchDeletion()
  @Override
  public int deleteById(final Long id) {
    return genericPojoDao.delete(id, dtoClass);
  }

  @Override
  public int deleteByIds(final List<Long> ids) {
    return genericPojoDao.delete(ids, dtoClass);
  }

  @Override
  public int deleteByPredicate(final Predicate predicate) {
    return genericPojoDao.deleteByPredicate(predicate, dtoClass);
  }

  @Override
  @Transactional
  public int deleteRecordsOlderThanDays(final int days) {
    final DateTime expireDate = new DateTime().minusDays(days);
    final Timestamp expireTimestamp = new Timestamp(expireDate.getMillis());
    final Predicate timestampPredicate = Predicate.LT("createTime", expireTimestamp);
    return deleteByPredicate(timestampPredicate);
  }

  @Override
  public List<E> findAll() {
    return (List<E>) genericPojoDao.getAll(dtoClass);
  }

  @Override
  public List<E> findByParams(final Map<String, Object> filters) {
    return (List<E>) genericPojoDao.get(filters, dtoClass);
  }

  @Override
  public List<E> findByPredicate(final Predicate predicate) {
    return (List<E>) genericPojoDao.get(predicate, dtoClass);
  }

  @Override
  public List<E> filter(final DaoFilter daoFilter) {
    return genericPojoDao.filter(daoFilter.setBeanClass(dtoClass));
  }

  @Override
  public long count() {
    return genericPojoDao.count(dtoClass);
  }
}