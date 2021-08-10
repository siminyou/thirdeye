import { render, screen } from "@testing-library/react";
import React from "react";
import { MemoryRouter } from "react-router-dom";
import { LoadingIndicator } from "../../components/loading-indicator/loading-indicator.component";
import { AppRoute } from "../../utils/routes/routes.util";
import { DatasourcesRouter } from "./datasources.router";

jest.mock(
    "../../components/app-breadcrumbs/app-breadcrumbs-provider/app-breadcrumbs-provider.component",
    () => ({
        useAppBreadcrumbs: jest.fn().mockImplementation(() => ({
            setRouterBreadcrumbs: mockSetRouterBreadcrumbs,
        })),
    })
);

jest.mock("react-router-dom", () => ({
    ...(jest.requireActual("react-router-dom") as Record<string, unknown>),
    useHistory: jest.fn().mockImplementation(() => ({
        push: mockPush,
    })),
}));

jest.mock("react-i18next", () => ({
    useTranslation: jest.fn().mockReturnValue({
        t: (key: string) => key,
    }),
}));

jest.mock("../../utils/routes/routes.util", () => ({
    ...(jest.requireActual("../../utils/routes/routes.util") as Record<
        string,
        unknown
    >),
    getDatasourcesPath: jest.fn().mockReturnValue("testDatasourcesPath"),
}));

jest.mock(
    "../../components/loading-indicator/loading-indicator.component",
    () => ({
        LoadingIndicator: jest.fn().mockReturnValue("testLoadingIndicator"),
    })
);

jest.mock(
    "../../pages/datasources-all-page/datasources-all-page.component",
    () => ({
        DatasourcesAllPage: jest.fn().mockReturnValue("testDatasourcesAllPage"),
    })
);

jest.mock(
    "../../pages/datasources-view-page/datasources-view-page.component",
    () => ({
        DatasourcesViewPage: jest
            .fn()
            .mockReturnValue("testDatasourcesViewPage"),
    })
);

jest.mock(
    "../../pages/datasources-create-page/datasources-create-page.component",
    () => ({
        DatasourcesCreatePage: jest
            .fn()
            .mockReturnValue("testDatasourcesCreatePage"),
    })
);

jest.mock(
    "../../pages/datasources-update-page/datasources-update-page.component",
    () => ({
        DatasourcesUpdatePage: jest
            .fn()
            .mockReturnValue("testDatasourcesUpdatePage"),
    })
);

jest.mock(
    "../../pages/page-not-found-page/page-not-found-page.component",
    () => ({
        PageNotFoundPage: jest.fn().mockReturnValue("testPageNotFoundPage"),
    })
);

describe("Datasources Router", () => {
    it("should have rendered loading indicator while loading", () => {
        render(
            <MemoryRouter>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        expect(LoadingIndicator).toHaveBeenCalled();
    });

    it("should set appropriate router breadcrumbs", () => {
        render(
            <MemoryRouter>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        expect(mockSetRouterBreadcrumbs).toHaveBeenCalled();

        // Get router breadcrumbs
        const breadcrumbs = mockSetRouterBreadcrumbs.mock.calls[0][0];
        // Also invoke the click handlers
        breadcrumbs &&
            breadcrumbs[0] &&
            breadcrumbs[0].onClick &&
            breadcrumbs[0].onClick();

        expect(breadcrumbs).toHaveLength(1);
        expect(breadcrumbs[0].text).toEqual("label.datasources");
        expect(breadcrumbs[0].onClick).toBeDefined();
        expect(mockPush).toHaveBeenCalledWith("testDatasourcesPath");
    });

    it("should render datasources all page at exact datasources path", async () => {
        render(
            <MemoryRouter initialEntries={[AppRoute.DATASOURCES]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testDatasourcesAllPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page at invalid datasources path", async () => {
        render(
            <MemoryRouter initialEntries={[`${AppRoute.DATASOURCES}/testPath`]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render datasources all page at exact datasources all path", async () => {
        render(
            <MemoryRouter initialEntries={[AppRoute.DATASOURCES_ALL]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testDatasourcesAllPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page at invalid datasources all path", async () => {
        render(
            <MemoryRouter
                initialEntries={[`${AppRoute.DATASOURCES_ALL}/testPath`]}
            >
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render datasources view page at exact datasources view path", async () => {
        render(
            <MemoryRouter initialEntries={[AppRoute.DATASOURCES_VIEW]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testDatasourcesViewPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page at invalid datasources view path", async () => {
        render(
            <MemoryRouter
                initialEntries={[`${AppRoute.DATASOURCES_VIEW}/testPath`]}
            >
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render datasources create page at exact datasources create path", async () => {
        render(
            <MemoryRouter initialEntries={[AppRoute.DATASOURCES_CREATE]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testDatasourcesCreatePage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page at invalid datasources create path", async () => {
        render(
            <MemoryRouter
                initialEntries={[`${AppRoute.DATASOURCES_CREATE}/testPath`]}
            >
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render datasources update page at exact datasources update path", async () => {
        render(
            <MemoryRouter initialEntries={[AppRoute.DATASOURCES_UPDATE]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testDatasourcesUpdatePage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page at invalid datasources update path", async () => {
        render(
            <MemoryRouter
                initialEntries={[`${AppRoute.DATASOURCES_UPDATE}/testPath`]}
            >
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page at any other path", async () => {
        render(
            <MemoryRouter initialEntries={["/testPath"]}>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });

    it("should render page not found page by default", async () => {
        render(
            <MemoryRouter>
                <DatasourcesRouter />
            </MemoryRouter>
        );

        await expect(
            screen.findByText("testPageNotFoundPage")
        ).resolves.toBeInTheDocument();
    });
});

const mockSetRouterBreadcrumbs = jest.fn();

const mockPush = jest.fn();