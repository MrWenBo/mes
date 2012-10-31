/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.2.0-SNAPSHOT
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.deliveries.hooks;

import static com.qcadoo.mes.deliveries.constants.OrderedProductFields.DELIVERY;
import static com.qcadoo.mes.deliveries.constants.OrderedProductFields.PRODUCT;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.qcadoo.mes.deliveries.constants.DeliveredProductFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;

@Service
public class DeliveredProductHooks {

    public boolean checkIfDeliveredProductAlreadyExists(final DataDefinition dataDefinition, final Entity entity) {
        SearchCriteriaBuilder searchCriteriaBuilder = dataDefinition.find()
                .add(SearchRestrictions.belongsTo(DELIVERY, entity.getBelongsToField(DELIVERY)))
                .add(SearchRestrictions.belongsTo(PRODUCT, entity.getBelongsToField(PRODUCT)));

        if (entity.getId() != null) {
            searchCriteriaBuilder.add(SearchRestrictions.ne("id", entity.getId()));
        }
        Entity orderedProductFromDB = searchCriteriaBuilder.uniqueResult();
        if (orderedProductFromDB == null) {
            return true;
        } else {
            entity.addError(dataDefinition.getField(PRODUCT), "deliveries.delivedProduct.error.alreadyExists");
            return false;
        }
    }

    public boolean checkIfDeliveredQuantityIsLessThanDamagedQuantity(final DataDefinition dataDefinition, final Entity entity) {
        BigDecimal damagedQuantity = entity.getDecimalField(DeliveredProductFields.DAMAGED_QUANTITY);
        BigDecimal deliveredQuantity = entity.getDecimalField(DeliveredProductFields.DELIVERED_QUANTITY);

        if (damagedQuantity == null || deliveredQuantity == null) {
            return true;
        }
        if (damagedQuantity.compareTo(deliveredQuantity) == 1) {
            entity.addError(dataDefinition.getField(DeliveredProductFields.DAMAGED_QUANTITY),
                    "deliveries.delivedProduct.error.damagedQuantity.deliveredQuantityIsTooMuch");
            entity.addError(dataDefinition.getField(DeliveredProductFields.DELIVERED_QUANTITY),
                    "deliveries.delivedProduct.error.damagedQuantity.deliveredQuantityIsTooMuch");
            return false;
        }
        return true;
    }

}