-- V10: Seed full i18n translations for existing modules
-- This ensures that once we switch to t(), the text is already available in DB

-- Cleanup existing V7 seeds to avoid duplicates with better keys
DELETE FROM i18n_translations WHERE namespace IN ('common', 'auth');

INSERT INTO i18n_translations (locale, namespace, translation_key, translation_value) VALUES
    -- Common (EN)
    ('en', 'common', 'btn.save', 'Save'),
    ('en', 'common', 'btn.cancel', 'Cancel'),
    ('en', 'common', 'btn.delete', 'Delete'),
    ('en', 'common', 'btn.edit', 'Edit'),
    ('en', 'common', 'btn.submit', 'Submit'),
    ('en', 'common', 'btn.confirm', 'Confirm'),
    ('en', 'common', 'btn.search', 'Search'),
    ('en', 'common', 'btn.close', 'Close'),
    ('en', 'common', 'label.actions', 'Actions'),
    ('en', 'common', 'label.status', 'Status'),
    ('en', 'common', 'label.created_at', 'Created At'),
    ('en', 'common', 'label.search_placeholder', 'Search something...'),
    ('en', 'common', 'msg.loading', 'Loading...'),
    ('en', 'common', 'msg.no_data', 'No data found'),
    ('en', 'common', 'msg.success', 'Operation successful'),
    ('en', 'common', 'msg.error', 'An error occurred'),

    -- Common (VI)
    ('vi', 'common', 'btn.save', 'Lưu'),
    ('vi', 'common', 'btn.cancel', 'Hủy'),
    ('vi', 'common', 'btn.delete', 'Xóa'),
    ('vi', 'common', 'btn.edit', 'Sửa'),
    ('vi', 'common', 'btn.submit', 'Xác nhận'),
    ('vi', 'common', 'btn.confirm', 'Xác nhận'),
    ('vi', 'common', 'btn.search', 'Tìm kiếm'),
    ('vi', 'common', 'btn.close', 'Đóng'),
    ('vi', 'common', 'label.actions', 'Thao tác'),
    ('vi', 'common', 'label.status', 'Trạng thái'),
    ('vi', 'common', 'label.created_at', 'Ngày tạo'),
    ('vi', 'common', 'label.search_placeholder', 'Tìm kiếm...'),
    ('vi', 'common', 'msg.loading', 'Đang tải...'),
    ('vi', 'common', 'msg.no_data', 'Không tìm thấy dữ liệu'),
    ('vi', 'common', 'msg.success', 'Thao tác thành công'),
    ('vi', 'common', 'msg.error', 'Đã xảy ra lỗi'),

    -- Admin Layout (EN)
    ('en', 'admin.layout', 'sidebar.dashboard', 'Dashboard'),
    ('en', 'admin.layout', 'sidebar.inventory', 'Inventory Ecosystem'),
    ('en', 'admin.layout', 'sidebar.sales', 'Sales Feed'),
    ('en', 'admin.layout', 'sidebar.customers', 'Client Relations'),
    ('en', 'admin.layout', 'sidebar.media', 'Media Explorer'),
    ('en', 'admin.layout', 'sidebar.system', 'Team Governance'),
    ('en', 'admin.layout', 'sidebar.roles', 'Access Roles'),
    ('en', 'admin.layout', 'sidebar.i18n', 'Translations'),
    ('en', 'admin.layout', 'header.intelligence_portal', 'Intelligence Portal'),
    ('en', 'admin.layout', 'header.core_network', 'Core Network'),
    ('en', 'admin.layout', 'header.intelligence', 'Intelligence'),
    ('en', 'admin.layout', 'header.sign_out', 'Sign Out securely'),

    -- Admin Layout (VI)
    ('vi', 'admin.layout', 'sidebar.dashboard', 'Bảng điều khiển'),
    ('vi', 'admin.layout', 'sidebar.inventory', 'Hệ sinh thái kho'),
    ('vi', 'admin.layout', 'sidebar.sales', 'Nguồn cấp doanh số'),
    ('vi', 'admin.layout', 'sidebar.customers', 'Quan hệ khách hàng'),
    ('vi', 'admin.layout', 'sidebar.media', 'Khám phá phương tiện'),
    ('vi', 'admin.layout', 'sidebar.system', 'Quản trị hệ thống'),
    ('vi', 'admin.layout', 'sidebar.roles', 'Vai trò truy cập'),
    ('vi', 'admin.layout', 'sidebar.i18n', 'Đa ngôn ngữ'),
    ('vi', 'admin.layout', 'header.intelligence_portal', 'Cổng thông tin trí tuệ'),
    ('vi', 'admin.layout', 'header.core_network', 'Mạng lưới cốt lõi'),
    ('vi', 'admin.layout', 'header.intelligence', 'Trí tuệ kinh doanh'),
    ('vi', 'admin.layout', 'header.sign_out', 'Đăng xuất an toàn'),

    -- Auth (EN)
    ('en', 'auth', 'login.title', 'Welcome back'),
    ('en', 'auth', 'login.subtitle', 'Access your account'),
    ('en', 'auth', 'login.btn', 'Login'),
    ('en', 'auth', 'register.title', 'Join Now'),
    ('en', 'auth', 'register.btn', 'Register'),

    -- Auth (VI)
    ('vi', 'auth', 'login.title', 'Chào mừng trở lại'),
    ('vi', 'auth', 'login.subtitle', 'Truy cập vào tài khoản của bạn'),
    ('vi', 'auth', 'login.btn', 'Đăng nhập'),
    ('vi', 'auth', 'register.title', 'Tham gia ngay'),
    ('vi', 'auth', 'register.btn', 'Đăng ký');
