package com.openease.service.image.data.dao.image;

import com.openease.common.data.dao.base.BaseDao;
import com.openease.common.data.model.image.ImageBinary;
import com.openease.common.data.model.image.ImageType;

import java.util.Optional;

/**
 * Image binary DAO
 *
 * @author Alan Czajkowski
 */
public interface ImageBinaryDao extends BaseDao<ImageBinary> {

  Optional<ImageBinary> findByImageIdAndType(String imageId, ImageType type);

}
