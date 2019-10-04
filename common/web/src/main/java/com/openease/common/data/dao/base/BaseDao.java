package com.openease.common.data.dao.base;

import com.openease.common.data.exception.GeneralDataException;
import com.openease.common.data.model.base.BaseAuditDataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;
import java.util.Optional;

import static com.openease.common.data.lang.MessageKeys.CRUD_UPDATE_STALE;
import static org.apache.commons.lang3.ObjectUtils.notEqual;

/**
 * Base DAO
 *
 * @author Alan Czajkowski
 */
@NoRepositoryBean
public interface BaseDao<M extends BaseAuditDataModel> extends ElasticsearchRepository<M, String> {

  default M update(M newer) throws GeneralDataException {
    final Logger LOG = LogManager.getLogger(BaseDao.class);
    LOG.trace("Updating: {}", () -> (newer == null ? null : newer.toStringUsingMixIn()));

    // check staleness via optimistic locking method
    M current;
    Optional<M> result = findById(newer.getId());
    if (result.isPresent()) {
      current = result.get();
      if (notEqual(current.getLastModified(), newer.getLastModified())) {
        throw new GeneralDataException(CRUD_UPDATE_STALE, "Object is stale, re-fetch before updating");
      }
    }

    newer.setLastModified(new Date());
    save(newer);

    return newer;
  }

}
