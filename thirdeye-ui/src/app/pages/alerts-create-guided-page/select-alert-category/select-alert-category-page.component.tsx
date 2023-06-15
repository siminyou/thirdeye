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
import {
    Box,
    Button,
    Card,
    CardContent,
    CardProps,
    Grid,
    Typography,
} from "@material-ui/core";
import { some } from "lodash";
import { default as React, FunctionComponent, useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Link, useOutletContext } from "react-router-dom";
import { generateAvailableAlgorithmOptions } from "../../../components/alert-wizard-v3/alert-type-selection/alert-type-selection.utils";
import {
    QUERY_PARAM_KEY_FOR_SAMPLE_ALERT_FILTER,
    SAMPLE_ALERT_TYPES,
} from "../../../components/alert-wizard-v3/sample-alert-selection/sample-alert-selection.interfaces";
import { generateOptions } from "../../../components/alert-wizard-v3/sample-alert-selection/sample-alert.utils";
import { PageContentsGridV1 } from "../../../platform/components";
import { useGetDatasets } from "../../../rest/datasets/datasets.actions";
import { AppRouteRelative } from "../../../utils/routes/routes.util";
import { AlertCreatedGuidedPageOutletContext } from "../alerts-create-guided-page.interfaces";

const OutlineCardComponent: FunctionComponent<CardProps> = (props) => {
    return <Card variant="outlined" {...props} />;
};

export const SelectAlertCategoryPage: FunctionComponent = () => {
    const { t } = useTranslation();
    const { datasets, getDatasets } = useGetDatasets();

    const { alertTemplates, setShouldShowStepper } =
        useOutletContext<AlertCreatedGuidedPageOutletContext>();

    const [hasBasicAlertSamples, hasMultiDimensionSamples] = useMemo(() => {
        if (datasets === null || alertTemplates === null) {
            return [false, false];
        }

        const sampleAlertOptions = generateOptions(datasets, alertTemplates);

        return [
            some(
                sampleAlertOptions.map(
                    (option) => option.isDimensionExploration === false
                )
            ),
            some(
                sampleAlertOptions.map(
                    (option) => option.isDimensionExploration === true
                )
            ),
        ];
    }, [datasets, alertTemplates]);

    useEffect(() => {
        setShouldShowStepper(false);
        getDatasets();

        // When this page is unloaded show the stepper
        return () => {
            setShouldShowStepper(true);
        };
    }, []);

    const hasMultiDimension = useMemo(() => {
        const alertTypeOptions = generateAvailableAlgorithmOptions(
            alertTemplates.map((a) => a.name)
        );

        return some(alertTypeOptions.map((option) => option.hasMultidimension));
    }, [alertTemplates]);

    return (
        <PageContentsGridV1>
            <Grid item xs={12}>
                <Box textAlign="center">
                    <Card>
                        <CardContent>
                            <Grid
                                container
                                alignContent="stretch"
                                justifyContent="space-around"
                            >
                                <Grid item md={5} sm={5} xs={12}>
                                    <Box
                                        component={OutlineCardComponent}
                                        display="flex"
                                        flexDirection="column"
                                        height="100%"
                                        justifyContent="space-between"
                                    >
                                        <Box>
                                            <CardContent>
                                                <Typography variant="h6">
                                                    {t("label.basic-alert")}
                                                </Typography>
                                            </CardContent>
                                        </Box>
                                        <Box>
                                            <CardContent>
                                                <Typography variant="body2">
                                                    {t(
                                                        "message.alert-on-anomalies-for-a-metric"
                                                    )}
                                                </Typography>
                                            </CardContent>
                                        </Box>
                                        <Box>
                                            <CardContent>
                                                <Button
                                                    color="primary"
                                                    component={Link}
                                                    to={
                                                        AppRouteRelative.WELCOME_CREATE_ALERT_SELECT_METRIC
                                                    }
                                                >
                                                    {t("label.create")}
                                                </Button>
                                                {hasBasicAlertSamples && (
                                                    <>
                                                        <Box pt={2}>or</Box>
                                                        <Button
                                                            color="primary"
                                                            component={Link}
                                                            disabled={
                                                                !hasBasicAlertSamples
                                                            }
                                                            size="small"
                                                            to={`${AppRouteRelative.WELCOME_CREATE_ALERT_SAMPLE_ALERT}?${QUERY_PARAM_KEY_FOR_SAMPLE_ALERT_FILTER}=${SAMPLE_ALERT_TYPES.BASIC}`}
                                                            variant="text"
                                                        >
                                                            {t(
                                                                "label.create-sample-alert"
                                                            )}
                                                        </Button>
                                                    </>
                                                )}
                                            </CardContent>
                                        </Box>
                                    </Box>
                                </Grid>
                                <Grid item md={5} sm={5} xs={12}>
                                    <Box
                                        component={OutlineCardComponent}
                                        display="flex"
                                        flexDirection="column"
                                        height="100%"
                                        justifyContent="space-between"
                                    >
                                        <Box>
                                            <CardContent>
                                                <Typography variant="h6">
                                                    {t(
                                                        "label.multidimensional-alert"
                                                    )}
                                                </Typography>
                                            </CardContent>
                                        </Box>
                                        <Box>
                                            <CardContent>
                                                <Typography variant="body2">
                                                    {t(
                                                        "message.alert-on-anomalies-on-multiple-dimensions-for-a-me"
                                                    )}
                                                </Typography>
                                            </CardContent>
                                        </Box>
                                        <Box>
                                            <CardContent>
                                                <Button
                                                    color="primary"
                                                    component={Link}
                                                    disabled={
                                                        !hasMultiDimension
                                                    }
                                                    to={
                                                        AppRouteRelative.WELCOME_CREATE_ALERT_SETUP_DIMENSION_EXPLORATION
                                                    }
                                                >
                                                    {t("label.create")}
                                                </Button>
                                                {hasMultiDimensionSamples && (
                                                    <>
                                                        <Box pt={2}>or</Box>
                                                        <Button
                                                            color="primary"
                                                            component={Link}
                                                            disabled={
                                                                !hasMultiDimension
                                                            }
                                                            size="small"
                                                            to={`${AppRouteRelative.WELCOME_CREATE_ALERT_SAMPLE_ALERT}?${QUERY_PARAM_KEY_FOR_SAMPLE_ALERT_FILTER}=${SAMPLE_ALERT_TYPES.MULTIDIMENSION}`}
                                                            variant="text"
                                                        >
                                                            {t(
                                                                "label.create-sample-alert"
                                                            )}
                                                        </Button>
                                                    </>
                                                )}
                                            </CardContent>
                                        </Box>
                                    </Box>
                                </Grid>
                            </Grid>
                        </CardContent>
                        {/** intentionally left empty for padding */}
                        <CardContent />
                    </Card>
                </Box>
            </Grid>
        </PageContentsGridV1>
    );
};