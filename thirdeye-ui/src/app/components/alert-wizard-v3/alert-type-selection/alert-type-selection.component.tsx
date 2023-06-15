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
 *
 * See the License for the specific language governing permissions and limitations under
 * the License.
 */
import { Grid, Typography } from "@material-ui/core";
import React, { FunctionComponent, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { PageContentsCardV1 } from "../../../platform/components";
import { AlertTemplatesInformationLinks } from "../alert-templates-information-links/alert-templates-information-links";
import { AlertTypeSection } from "./alert-type-section/alert-type-section.component";
import {
    AlertTypeSelectionProps,
    AlgorithmOption,
} from "./alert-type-selection.interfaces";
import { generateAvailableAlgorithmOptions } from "./alert-type-selection.utils";

export const AlertTypeSelection: FunctionComponent<AlertTypeSelectionProps> = ({
    onAlertPropertyChange,
    onSelectionComplete,
    alertTemplates,
}) => {
    const { t } = useTranslation();

    const handleAlgorithmClick = (algorithmOption: AlgorithmOption): void => {
        onAlertPropertyChange({
            template: {
                name: algorithmOption.alertTemplate,
            },
        });
        onSelectionComplete();
    };

    const options = useMemo(() => {
        return generateAvailableAlgorithmOptions(
            alertTemplates.map((a) => a.name)
        );
    }, [alertTemplates]);

    return (
        <PageContentsCardV1>
            <Grid container alignItems="stretch">
                <Grid item xs={12}>
                    <Typography variant="h5">
                        {t(
                            "message.select-the-algorithm-type-best-fit-for-your-alert"
                        )}
                    </Typography>
                    <AlertTemplatesInformationLinks />
                </Grid>

                {options.map((option) => {
                    return (
                        <Grid item key={option.algorithmOption.title} xs={12}>
                            <AlertTypeSection
                                option={option}
                                onClick={handleAlgorithmClick}
                            />
                        </Grid>
                    );
                })}
            </Grid>
        </PageContentsCardV1>
    );
};