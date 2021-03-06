/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.4
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
package com.qcadoo.mes.productionCounting.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.productionCounting.SetTechnologyInComponentsService;
import com.qcadoo.mes.productionCounting.listeners.TrackingOperationProductComponentDetailsListeners;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.CheckBoxComponent;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;

@Service
public class TrackingOperationProductInComponentDetailsHooks {

    private static final String L_FORM = "form";

    private static final String L_SET_TAB = "setTab";

    private static final String L_WASTE_USED = "wasteUsed";

    private static final String L_WASTE_USED_ONLY = "wasteUsedOnly";

    private static final String L_WASTE_USED_QUANTITY = "wasteUsedQuantity";

    private static final String L_WASTE_UNIT = "wasteUnit";

    private static final String L_USED_QUANTITY = "usedQuantity";

    private static final String L_GIVEN_QUANTITY = "givenQuantity";

    @Autowired
    private TrackingOperationProductComponentDetailsListeners trackingOperationProductComponentDetailsListeners;

    @Autowired
    private SetTechnologyInComponentsService setTechnologyInComponentsService;

    public void onBeforeRender(final ViewDefinitionState view) {
        trackingOperationProductComponentDetailsListeners.onBeforeRender(view);

        FormComponent trackingOperationProductInComponentForm = (FormComponent) view.getComponentByReference(L_FORM);
        Entity trackingOperationProductInComponent = trackingOperationProductInComponentForm
                .getPersistedEntityWithIncludedFormValues();

        toggleEnabledForWastes(view);
        hideOrShowSetTab(view, trackingOperationProductInComponent);
    }

    private void toggleEnabledForWastes(final ViewDefinitionState view) {
        CheckBoxComponent wasteUsed = (CheckBoxComponent) view.getComponentByReference(L_WASTE_USED);
        CheckBoxComponent wasteUsedOnly = (CheckBoxComponent) view.getComponentByReference(L_WASTE_USED_ONLY);
        FieldComponent wasteUsedQuantity = (FieldComponent) view.getComponentByReference(L_WASTE_USED_QUANTITY);
        FieldComponent wasteUnit = (FieldComponent) view.getComponentByReference(L_WASTE_UNIT);
        if (wasteUsed.isChecked()) {
            wasteUsedOnly.setEnabled(true);
            wasteUsedQuantity.setEnabled(true);
            wasteUnit.setEnabled(true);
        } else {
            wasteUsedOnly.setEnabled(false);
            wasteUsedQuantity.setEnabled(false);
            wasteUnit.setEnabled(false);
        }

        FieldComponent usedQuantity = (FieldComponent) view.getComponentByReference(L_USED_QUANTITY);
        FieldComponent givenQuantity = (FieldComponent) view.getComponentByReference(L_GIVEN_QUANTITY);
        if (wasteUsedOnly.isChecked()) {
            usedQuantity.setEnabled(false);
            givenQuantity.setEnabled(false);
        } else {
            usedQuantity.setEnabled(true);
            givenQuantity.setEnabled(true);
        }
    }

    private void hideOrShowSetTab(final ViewDefinitionState view, final Entity trackingOperationProductInComponent) {
        view.getComponentByReference(L_SET_TAB).setVisible(
                setTechnologyInComponentsService.isSet(trackingOperationProductInComponent));
    }

}
