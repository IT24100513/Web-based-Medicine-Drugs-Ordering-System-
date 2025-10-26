// Admin Dashboard JavaScript

// Test if JavaScript is loading
console.log('Admin JavaScript loaded successfully');

// Wait for DOM to be ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing user management functions');
    
    // Test if functions are available
    console.log('viewUser function available:', typeof viewUser === 'function');
    console.log('editUser function available:', typeof editUser === 'function');
    console.log('deleteUser function available:', typeof deleteUser === 'function');
    console.log('toggleUserStatus function available:', typeof toggleUserStatus === 'function');
    console.log('toggleDropdown function available:', typeof toggleDropdown === 'function');
});

// User Management Functions
function viewUser(userId) {
    console.log('viewUser called with ID:', userId);
    // Fetch user details and show in modal
    fetch('/admin/users/api/' + userId)
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(user => {
            console.log('User data received:', user);
            showUserDetailsModal(user);
        })
        .catch(error => {
            console.error('Error fetching user details:', error);
            showToast('Error loading user details: ' + error.message, 'error');
        });
}

function showUserDetailsModal(user) {
    // Create modal HTML
    const modalHTML = `
        <div id="userDetailsModal" class="modal-overlay" onclick="closeUserDetailsModal()">
            <div class="modal-content" onclick="event.stopPropagation()">
                <div class="modal-header">
                    <h3>User Details</h3>
                    <button class="modal-close" onclick="closeUserDetailsModal()">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="user-details-grid">
                        <div class="detail-item">
                            <label>ID:</label>
                            <span>${user.id}</span>
                        </div>
                        <div class="detail-item">
                            <label>Name:</label>
                            <span>${user.firstName} ${user.lastName}</span>
                        </div>
                        <div class="detail-item">
                            <label>Username:</label>
                            <span>${user.username}</span>
                        </div>
                        <div class="detail-item">
                            <label>Email:</label>
                            <span>${user.email}</span>
                        </div>
                        <div class="detail-item">
                            <label>Phone:</label>
                            <span>${user.phoneNumber || 'Not provided'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Role:</label>
                            <span class="role-badge role-${user.role.toLowerCase()}">${user.role.replace('_', ' ')}</span>
                        </div>
                        <div class="detail-item">
                            <label>Status:</label>
                            <span class="status-badge ${user.active ? 'status-active' : 'status-inactive'}">${user.active ? 'Active' : 'Inactive'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Created:</label>
                            <span>${user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'Not available'}</span>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" onclick="closeUserDetailsModal()">Close</button>
                    <button class="btn btn-primary" onclick="editUser(${user.id}); closeUserDetailsModal();">Edit User</button>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHTML);
}

function closeUserDetailsModal() {
    const modal = document.getElementById('userDetailsModal');
    if (modal) {
        modal.remove();
    }
}

function editUser(userId) {
    console.log('editUser called with ID:', userId);
    window.location.href = '/admin/users/edit/' + userId;
}

function deleteUser(userId) {
    console.log('deleteUser called with ID:', userId);
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
        window.location.href = '/admin/users/delete/' + userId;
    }
}

function toggleUserStatus(userId) {
    console.log('toggleUserStatus called with ID:', userId);
    if (confirm('Are you sure you want to toggle this user\'s status?')) {
        window.location.href = '/admin/users/toggle/' + userId;
    }
}

// Dropdown functionality
function toggleDropdown(button) {
    console.log('toggleDropdown called');
    // Close all other dropdowns
    const allDropdowns = document.querySelectorAll('.dropdown-menu');
    allDropdowns.forEach(dropdown => {
        if (dropdown !== button.nextElementSibling) {
            dropdown.style.display = 'none';
        }
    });
    
    // Toggle current dropdown
    const dropdown = button.nextElementSibling;
    if (dropdown) {
        const isVisible = dropdown.style.display === 'block';
        dropdown.style.display = isVisible ? 'none' : 'block';
        console.log('Dropdown toggled, now visible:', !isVisible);
    } else {
        console.log('No dropdown found next to button');
    }
}

// Close dropdowns when clicking outside
document.addEventListener('click', function(event) {
    if (!event.target.closest('.action-dropdown')) {
        const allDropdowns = document.querySelectorAll('.dropdown-menu');
        allDropdowns.forEach(dropdown => {
            dropdown.style.display = 'none';
        });
    }
});

// Toast notification functions
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    const toastMessage = document.getElementById('toast-message');
    
    toastMessage.textContent = message;
    toast.classList.add('show');
    
    // Auto hide after 5 seconds
    setTimeout(() => {
        hideToast();
    }, 5000);
}

function hideToast() {
    const toast = document.getElementById('toast');
    toast.classList.remove('show');
}

// Password toggle functionality
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const passwordIcon = document.getElementById('password-icon');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        passwordIcon.classList.remove('fa-eye');
        passwordIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        passwordIcon.classList.remove('fa-eye-slash');
        passwordIcon.classList.add('fa-eye');
    }
}

// Table row selection
function toggleSelectAll() {
    const selectAllCheckbox = document.querySelector('thead .checkbox');
    const rowCheckboxes = document.querySelectorAll('tbody .checkbox');
    
    rowCheckboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
}

// Individual row selection
function toggleRowSelection(checkbox) {
    const selectAllCheckbox = document.querySelector('thead .checkbox');
    const rowCheckboxes = document.querySelectorAll('tbody .checkbox');
    const checkedBoxes = document.querySelectorAll('tbody .checkbox:checked');
    
    if (checkedBoxes.length === rowCheckboxes.length) {
        selectAllCheckbox.checked = true;
        selectAllCheckbox.indeterminate = false;
    } else if (checkedBoxes.length > 0) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = true;
    } else {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
    }
}

// Search functionality
function initializeSearch() {
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const tableRows = document.querySelectorAll('tbody tr');
            
            tableRows.forEach(row => {
                const username = row.querySelector('.user-name').textContent.toLowerCase();
                const email = row.querySelector('.user-email').textContent.toLowerCase();
                
                if (username.includes(searchTerm) || email.includes(searchTerm)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    }
}

// Table sorting functionality
function initializeSorting() {
    const sortableHeaders = document.querySelectorAll('.sortable');
    
    sortableHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const column = this.cellIndex;
            const table = this.closest('table');
            const tbody = table.querySelector('tbody');
            const rows = Array.from(tbody.querySelectorAll('tr'));
            
            // Toggle sort direction
            const isAscending = !this.classList.contains('sort-asc');
            
            // Remove sort classes from all headers
            sortableHeaders.forEach(h => {
                h.classList.remove('sort-asc', 'sort-desc');
            });
            
            // Add appropriate sort class
            this.classList.add(isAscending ? 'sort-asc' : 'sort-desc');
            
            // Sort rows
            rows.sort((a, b) => {
                const aText = a.cells[column].textContent.trim();
                const bText = b.cells[column].textContent.trim();
                
                if (isAscending) {
                    return aText.localeCompare(bText);
                } else {
                    return bText.localeCompare(aText);
                }
            });
            
            // Reorder rows in table
            rows.forEach(row => tbody.appendChild(row));
        });
    });
}

// Form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.classList.add('error');
                } else {
                    field.classList.remove('error');
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                showToast('Please fill in all required fields', 'error');
            }
        });
    });
}

// Delete confirmation
function confirmDelete(username, deleteUrl) {
    if (confirm(`Are you sure you want to delete the admin user "${username}"?`)) {
        window.location.href = deleteUrl;
    }
}

// Edit admin user function
function editAdminUser(userId) {
    // Find the row that contains this user ID
    const rows = document.querySelectorAll('tbody tr');
    let userData = null;
    
    for (let row of rows) {
        const editLink = row.querySelector('.dropdown-item[onclick*="editAdminUser"]');
        if (editLink && editLink.getAttribute('onclick').includes(userId)) {
            const username = row.querySelector('.user-name').textContent;
            const email = row.querySelector('.user-email').textContent;
            const role = row.querySelector('.access-tag').textContent;
            userData = { username, email, role, id: userId };
            break;
        }
    }
    
    if (userData) {
        showEditModal(userData);
    } else {
        alert(`Edit user ID: ${userId}`);
    }
}

// View admin user details function
function viewAdminUser(userId) {
    // Find the row that contains this user ID
    const rows = document.querySelectorAll('tbody tr');
    let userData = null;
    
    for (let row of rows) {
        const editLink = row.querySelector('.dropdown-item[onclick*="editAdminUser"]');
        if (editLink && editLink.getAttribute('onclick').includes(userId)) {
            const username = row.querySelector('.user-name').textContent;
            const email = row.querySelector('.user-email').textContent;
            const role = row.querySelector('.access-tag').textContent;
            userData = { username, email, role };
            break;
        }
    }
    
    if (userData) {
        showUserDetailsModal(userData.username, userData.email, userData.role);
    } else {
        // Fallback: show alert with user ID
        alert(`View details for user ID: ${userId}`);
    }
}

// Show user details modal
function showUserDetailsModal(username, email, role) {
    // Create modal HTML
    const modalHtml = `
        <div id="userDetailsModal" class="modal-overlay" onclick="closeModal()">
            <div class="modal-content" onclick="event.stopPropagation()">
                <div class="modal-header">
                    <h3>User Details</h3>
                    <button class="modal-close" onclick="closeModal()">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="user-detail-item">
                        <label>Username:</label>
                        <span>${username}</span>
                    </div>
                    <div class="user-detail-item">
                        <label>Email:</label>
                        <span>${email}</span>
                    </div>
                    <div class="user-detail-item">
                        <label>Role:</label>
                        <span class="role-badge ${role === 'MANAGER' ? 'tag-admin' : 'tag-officer'}">${role}</span>
                    </div>
                    <div class="user-detail-item">
                        <label>Status:</label>
                        <span class="status-active">Active</span>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" onclick="closeModal()">Close</button>
                    <button class="btn btn-primary" onclick="editAdminUserFromModal('${username}')">Edit User</button>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to page
    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

// Close modal function
function closeModal() {
    const modal = document.getElementById('userDetailsModal');
    if (modal) {
        modal.remove();
    }
}

// Edit user from modal
function editAdminUserFromModal(username) {
    closeModal();
    // Find the user ID and redirect to edit
    const rows = document.querySelectorAll('tbody tr');
    for (let row of rows) {
        const rowUsername = row.querySelector('.user-name').textContent;
        if (rowUsername === username) {
            const userId = row.querySelector('.dropdown-item[onclick*="editAdminUser"]').getAttribute('onclick').match(/\d+/)[0];
            editAdminUser(userId);
            break;
        }
    }
}

// Show edit modal function
function showEditModal(userData) {
    // Create edit modal HTML
    const modalHtml = `
        <div id="editUserModal" class="modal-overlay" onclick="closeEditModal()">
            <div class="modal-content edit-modal" onclick="event.stopPropagation()">
                <div class="modal-header">
                    <h3>
                        <i class="fas fa-user-edit"></i>
                        Edit Admin User
                    </h3>
                    <button class="modal-close" onclick="closeEditModal()">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="editUserForm" class="edit-form" action="/admin/users/update" method="post">
                        <input type="hidden" id="editUserId" name="id" value="${userData.id}">
                        
                        <div class="form-group">
                            <label for="editUsername" class="form-label">
                                <i class="fas fa-user"></i>
                                Username
                            </label>
                            <input type="text" 
                                   id="editUsername" 
                                   name="username"
                                   class="form-input" 
                                   value="${userData.username}"
                                   placeholder="Enter username"
                                   required>
                            <div class="form-help">Choose a unique username for this admin user</div>
                        </div>

                        <div class="form-group">
                            <label for="editPassword" class="form-label">
                                <i class="fas fa-lock"></i>
                                Password
                            </label>
                            <input type="password" 
                                   id="editPassword" 
                                   name="password"
                                   class="form-input" 
                                   placeholder="Enter new password (leave blank to keep current)">
                            <div class="form-help">Leave blank to keep the current password</div>
                        </div>

                        <div class="form-group">
                            <label for="editRole" class="form-label">
                                <i class="fas fa-user-tag"></i>
                                Role
                            </label>
                            <select id="editRole" name="role" class="form-select" required>
                                <option value="">Select a role</option>
                                <option value="MANAGER" ${userData.role === 'MANAGER' ? 'selected' : ''}>MANAGER</option>
                                <option value="IT_OFFICER" ${userData.role === 'IT_OFFICER' ? 'selected' : ''}>IT_OFFICER</option>
                            </select>
                            <div class="form-help">Select the appropriate role for this admin user</div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" onclick="closeEditModal()">
                        <i class="fas fa-times"></i>
                        Cancel
                    </button>
                    <button type="button" class="btn btn-primary" onclick="updateAdminUser()">
                        <i class="fas fa-save"></i>
                        Update User
                    </button>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to page
    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

// Close edit modal function
function closeEditModal() {
    const modal = document.getElementById('editUserModal');
    if (modal) {
        modal.remove();
    }
}

// Update admin user function
function updateAdminUser() {
    console.log('Update button clicked'); // Debug log
    
    const form = document.getElementById('editUserForm');
    const userId = document.getElementById('editUserId').value;
    const username = document.getElementById('editUsername').value;
    const password = document.getElementById('editPassword').value;
    const role = document.getElementById('editRole').value;
    
    console.log('Form data:', { userId, username, password, role }); // Debug log
    
    // Clear previous errors
    clearEditErrors();
    
    // Validate form
    let isValid = true;
    
    if (!username.trim()) {
        showEditError('editUsername', 'Username is required');
        isValid = false;
    } else if (username.trim().length < 3) {
        showEditError('editUsername', 'Username must be at least 3 characters long');
        isValid = false;
    }
    
    if (!role) {
        showEditError('editRole', 'Please select a role');
        isValid = false;
    }
    
    if (!isValid) {
        showNotification('Please fix the errors above', 'error');
        return;
    }
    
    // Show loading state
    const updateButton = document.querySelector('#editUserModal .btn-primary');
    const originalText = updateButton.innerHTML;
    updateButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
    updateButton.disabled = true;
    
    // Try AJAX first, fallback to form submission
    try {
        // Prepare form data
        const formData = new FormData(form);
        
        console.log('Sending AJAX request to /admin/users/update'); // Debug log
        
        // Send update request
        fetch('/admin/users/update', {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            console.log('Response status:', response.status); // Debug log
            if (response.ok) {
                showNotification('Admin user updated successfully!', 'success');
                closeEditModal();
                // Reload the page to show updated data
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                throw new Error(`AJAX failed: ${response.status}`);
            }
        })
        .catch(error => {
            console.log('AJAX failed, trying form submission:', error); // Debug log
            // Fallback to form submission
            form.submit();
        });
    } catch (error) {
        console.log('AJAX error, using form submission:', error); // Debug log
        // Fallback to form submission
        form.submit();
    }
}

// Show edit error function
function showEditError(inputId, message) {
    const input = document.getElementById(inputId);
    input.classList.add('error');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'form-error';
    errorDiv.textContent = message;
    input.parentNode.appendChild(errorDiv);
}

// Clear edit errors function
function clearEditErrors() {
    const errorInputs = document.querySelectorAll('#editUserModal .form-input.error, #editUserModal .form-select.error');
    errorInputs.forEach(input => input.classList.remove('error'));
    
    const errorMessages = document.querySelectorAll('#editUserModal .form-error');
    errorMessages.forEach(error => error.remove());
}

// Dropdown functionality - Simplified approach
function initializeDropdowns() {
    const dropdowns = document.querySelectorAll('.dropdown');
    
    dropdowns.forEach((dropdown) => {
        const menu = dropdown.querySelector('.dropdown-menu');
        let hideTimeout;
        
        // Show dropdown on hover
        dropdown.addEventListener('mouseenter', function() {
            clearTimeout(hideTimeout);
            menu.style.display = 'block';
        });
        
        // Hide dropdown when leaving the dropdown area
        dropdown.addEventListener('mouseleave', function() {
            hideTimeout = setTimeout(() => {
                menu.style.display = 'none';
            }, 200); // Increased delay to 200ms
        });
        
        // Keep dropdown open when hovering over menu
        menu.addEventListener('mouseenter', function() {
            clearTimeout(hideTimeout);
        });
        
        // Hide dropdown when leaving menu
        menu.addEventListener('mouseleave', function() {
            hideTimeout = setTimeout(() => {
                menu.style.display = 'none';
            }, 200);
        });
    });
}

// Initialize all functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize search
    initializeSearch();
    
    // Initialize sorting
    initializeSorting();
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize dropdowns
    initializeDropdowns();
    
    // Initialize select all checkbox
    const selectAllCheckbox = document.querySelector('thead .checkbox');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', toggleSelectAll);
    }
    
    // Initialize individual row checkboxes
    const rowCheckboxes = document.querySelectorAll('tbody .checkbox');
    rowCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            toggleRowSelection(this);
        });
    });
    
    // Show success message if redirected from form submission
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'true') {
        showToast('Admin user created successfully!');
    }
    
    if (urlParams.get('deleted') === 'true') {
        showToast('Admin user deleted successfully!');
    }
});

// Add CSS for error states
const style = document.createElement('style');
style.textContent = `
    .form-input.error,
    .form-select.error {
        border-color: #ef4444;
        box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
    }
    
    .sort-asc::after {
        content: ' ↑';
        color: #3b82f6;
    }
    
    .sort-desc::after {
        content: ' ↓';
        color: #3b82f6;
    }
`;
document.head.appendChild(style);
