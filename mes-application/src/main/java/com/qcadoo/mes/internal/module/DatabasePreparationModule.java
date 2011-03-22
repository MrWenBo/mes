package com.qcadoo.mes.internal.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.transaction.annotation.Transactional;

import com.qcadoo.mes.application.TestDataLoader;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.Restrictions;
import com.qcadoo.plugin.api.PluginState;
import com.qcadoo.plugin.internal.api.Module;

public class DatabasePreparationModule extends Module {

    private static final Logger LOG = LoggerFactory.getLogger(DatabasePreparationModule.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TestDataLoader testDataLoader;

    @Value("${loadTestData}")
    private boolean addTestData;

    @Value("${showSystemInfo}")
    private boolean showSystemInfo;

    private final Map<String, Entity> menuCategories = new HashMap<String, Entity>();

    private Entity adminGroup;

    private Entity supervisorsGroup;

    @Value("${addAdministrationMenuToDatabase}")
    private boolean addAdministrationMenuToDatabase;

    @Value("${addHardAdminPass}")
    private boolean addHardAdminPass;

    @Override
    @Transactional
    public void init(PluginState state) {
        if (databaseHasToBePrepared()) {
            LOG.info("Database has to be prepared ...");

            createPersistenceLogins();

            addMenus();
            addGroups();
            addUsers();
            addParameters();

            if (addTestData) {
                testDataLoader.loadTestData();
            }
        } else {
            LOG.info("Database has been already prepared, skipping");
        }
    }

    private void createPersistenceLogins() {
        new JdbcTemplate(dataSource).execute(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
    }

    private void addMenus() {
        Entity menuCategoryGridView = getMenuViewDefinition("menuCategories");
        Entity technologyGridView = getMenuViewDefinition("technologies");
        Entity orderGridView = getMenuViewDefinition("orders");
        // Entity pluginGridView = getMenuViewDefinition("plugins");
        Entity userGridView = getMenuViewDefinition("users");
        Entity dictionaryGridView = getMenuViewDefinition("dictionaries");
        Entity materialRequirementGridView = getMenuViewDefinition("materialRequirements");
        Entity productGridView = getMenuViewDefinition("products");
        Entity groupGridView = getMenuViewDefinition("groups");
        Entity pluginGridView = getMenuViewDefinition("plugins");
        Entity machineGridView = getMenuViewDefinition("machines");
        Entity staffGridView = getMenuViewDefinition("staffs");
        Entity workPlanGridView = getMenuViewDefinition("workPlans");
        Entity operationGridView = getMenuViewDefinition("operations");
        Entity genealogyForComponentFormView = getMenuViewDefinition("genealogyForComponent");
        Entity genealogyForProductFormView = getMenuViewDefinition("genealogyForProduct");
        Entity qualityControlForOrderFormView = getMenuViewDefinition("qualityControlsForOrder");
        Entity qualityControlForUnitFormView = getMenuViewDefinition("qualityControlsForUnit");
        Entity qualityControlForBatchFormView = getMenuViewDefinition("qualityControlsForBatch");
        Entity qualityControlForOperationFormView = getMenuViewDefinition("qualityControlsForOperation");
        Entity qualityControlReportFormView = getMenuViewDefinition("qualityControlReport");

        Entity menuCategoryHome = addMenuCategory("home", "core.menu.home", 1);
        Entity menuCategoryBasicData = addMenuCategory("basic", "core.menu.basic", 2);
        Entity menuCategoryTechnology = addMenuCategory("technology", "core.menu.technology", 3);
        Entity menuCategoryOrders = addMenuCategory("orders", "core.menu.orders", 4);
        Entity menuCategoryReports = addMenuCategory("reports", "core.menu.reports", 5);
        Entity menuCategoryQuality = addMenuCategory("quality", "core.menu.quality", 6);
        Entity menuCategoryAdministration = addMenuCategory("administration", "core.menu.administration", 7);

        addMenuViewDefinitionItem("home", "core.menu.home", menuCategoryHome, getMenuViewDefinition("homePage"), 1);
        addMenuViewDefinitionItem("profile", "core.menu.profile", menuCategoryHome, getMenuViewDefinition("profile"), 2);

        if (showSystemInfo) {
            addMenuViewDefinitionItem("systemInfo", "core.menu.systemInfo", menuCategoryHome,
                    getMenuViewDefinition("systemInfoView"), 3);
        }

        addMenuViewDefinitionItem("technologies", "products.menu.products.technologies", menuCategoryTechnology,
                technologyGridView, 2);
        addMenuViewDefinitionItem("products", "products.menu.products.products", menuCategoryBasicData, productGridView, 4);

        addMenuViewDefinitionItem("productionOrders", "products.menu.products.productionOrders", menuCategoryOrders,
                orderGridView, 1);
        addMenuViewDefinitionItem("materialRequirements", "products.menu.products.materialRequirements", menuCategoryReports,
                materialRequirementGridView, 1);
        addMenuViewDefinitionItem("operations", "products.menu.products.operations", menuCategoryTechnology, operationGridView, 1);
        addMenuViewDefinitionItem("workPlans", "products.menu.products.workPlans", menuCategoryReports, workPlanGridView, 2);
        addMenuViewDefinitionItem("genealogyForComponent", "genealogies.menu.reports.genealogyForComponent", menuCategoryReports,
                genealogyForComponentFormView, 3);
        addMenuViewDefinitionItem("genealogyForProduct", "genealogies.menu.reports.genealogyForProduct", menuCategoryReports,
                genealogyForProductFormView, 4);
        addMenuViewDefinitionItem("qualityControlReport", "qualityControls.menu.reports.qualityControlReport",
                menuCategoryReports, qualityControlReportFormView, 5);
        addMenuViewDefinitionItem("systemParameters", "basic.menu.systemParameters", menuCategoryBasicData,
                getMenuViewDefinition("systemParameters"), 4);
        addMenuViewDefinitionItem("genealogyAttributes", "genealogy.menu.genealogyAttributes", menuCategoryBasicData,
                getMenuViewDefinition("genealogyAttributes"), 5);

        if (addAdministrationMenuToDatabase) {
            addMenuViewDefinitionItem("users", "users.menu.administration.users", menuCategoryAdministration, userGridView, 2);
            addMenuViewDefinitionItem("groups", "users.menu.administration.groups", menuCategoryAdministration, groupGridView, 3);
            addMenuViewDefinitionItem("plugins", "plugins.menu.administration.plugins", menuCategoryAdministration,
                    pluginGridView, 3);
            addMenuViewDefinitionItem("menu", "menu.menu.administration.menu", menuCategoryAdministration, menuCategoryGridView,
                    4);
        }

        addMenuViewDefinitionItem("dictionaries", "dictionaries.menu.administration.dictionaries", menuCategoryBasicData,
                dictionaryGridView, 1);
        addMenuViewDefinitionItem("machines", "basic.menu.machines", menuCategoryBasicData, machineGridView, 2);
        addMenuViewDefinitionItem("staff", "basic.menu.staff", menuCategoryBasicData, staffGridView, 3);

        addMenuViewDefinitionItem("forOrder", "products.menu.quality.forOrder", menuCategoryQuality,
                qualityControlForOrderFormView, 1);
        addMenuViewDefinitionItem("forUnits", "products.menu.quality.forUnits", menuCategoryQuality,
                qualityControlForUnitFormView, 2);
        addMenuViewDefinitionItem("forBatch", "products.menu.quality.forBatch", menuCategoryQuality,
                qualityControlForBatchFormView, 3);
        addMenuViewDefinitionItem("forOperation", "products.menu.quality.forOperation", menuCategoryQuality,
                qualityControlForOperationFormView, 4);
    }

    private void addMenuViewDefinitionItem(final String name, final String translation, final Entity menuCategory,
            final Entity menuViewDefinition, final int order) {
        LOG.info("Adding menu view item \"" + name + "\"");
        Entity menuItem = dataDefinitionService.get("menu", "menuViewDefinitionItem").create();
        menuItem.setField("itemOrder", order);
        menuItem.setField("menuCategory", menuCategory);
        menuItem.setField("name", name);
        menuItem.setField("active", true);
        menuItem.setField("translationName", translation);
        menuItem.setField("viewDefinition", menuViewDefinition);
        dataDefinitionService.get("menu", "menuViewDefinitionItem").save(menuItem);
    }

    private Entity getMenuViewDefinition(final String name) {
        List<Entity> menuList = dataDefinitionService.get("menu", "viewDefinition").find()
                .restrictedWith(Restrictions.eq("menuName", name)).withMaxResults(1).list().getEntities();
        if (menuList.isEmpty()) {
            return null;
        }
        return menuList.get(0);
    }

    private Entity addMenuCategory(final String name, final String translation, final int order) {
        if (menuCategories.containsKey(name)) {
            return menuCategories.get(name);
        }

        LOG.info("Adding menu category \"" + name + "\"");
        Entity category = dataDefinitionService.get("menu", "menuCategory").create();
        category.setField("name", name);
        category.setField("active", true);
        category.setField("translationName", translation);
        category.setField("categoryOrder", order);
        category = dataDefinitionService.get("menu", "menuCategory").save(category);
        menuCategories.put(name, category);
        return category;
    }

    private void addGroups() {
        adminGroup = addGroup("Admins", "ROLE_ADMIN");
        supervisorsGroup = addGroup("Supervisors", "ROLE_SUPERVISOR");
        addGroup("Users", "ROLE_USER");
    }

    private void addParameters() {
        LOG.info("Adding parameters");
        Entity parameter = dataDefinitionService.get("basic", "parameter").create();
        parameter.setField("checkDoneOrderForQuality", false);
        parameter.setField("autoGenerateQualityControl", false);
        parameter.setField("batchForDoneOrder", "01none");
        dataDefinitionService.get("basic", "parameter").save(parameter);
    }

    private void addUsers() {
        addUser("demo", "demo@email.com", "Demo", "Demo", "demo", supervisorsGroup);
        if (addHardAdminPass) {
            // TODO password
            addUser("admin", "admin@email.com", "Admin", "Admin", "qwe123", adminGroup);
        } else {
            addUser("admin", "admin@email.com", "Admin", "Admin", "admin", adminGroup);
        }
    }

    private void addUser(final String login, final String email, final String firstName, final String lastName,
            final String password, final Entity group) {
        LOG.info("Adding \"" + login + "\" user");
        Entity entity = dataDefinitionService.get("users", "user").create();
        entity.setField("userName", login);
        entity.setField("userGroup", group);
        entity.setField("email", email);
        entity.setField("firstName", firstName);
        entity.setField("lastName", lastName);
        entity.setField("password", password);
        entity.setField("passwordConfirmation", password);
        entity.setField("enabled", true);
        dataDefinitionService.get("users", "user").save(entity);
    }

    private Entity addGroup(final String name, final String role) {
        LOG.info("Adding group \"" + name + "\" with role \"" + role + "\"");
        Entity entity = dataDefinitionService.get("users", "group").create();
        entity.setField("name", name);
        entity.setField("role", role);
        return dataDefinitionService.get("users", "group").save(entity);
    }

    private boolean databaseHasToBePrepared() {
        return dataDefinitionService.get("users", "user").find().list().getTotalNumberOfEntities() == 0;
    }

    @Override
    public void enable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataDefinitionService(DataDefinitionService dataDefinitionService) {
        this.dataDefinitionService = dataDefinitionService;
    }

    public void setTestDataLoader(TestDataLoader testDataLoader) {
        this.testDataLoader = testDataLoader;
    }

    public void setAddTestData(boolean addTestData) {
        this.addTestData = addTestData;
    }

    public void setShowSystemInfo(boolean showSystemInfo) {
        this.showSystemInfo = showSystemInfo;
    }

    public void setAddAdministrationMenuToDatabase(boolean addAdministrationMenuToDatabase) {
        this.addAdministrationMenuToDatabase = addAdministrationMenuToDatabase;
    }

    public void setAddHardAdminPass(boolean addHardAdminPass) {
        this.addHardAdminPass = addHardAdminPass;
    }

}
