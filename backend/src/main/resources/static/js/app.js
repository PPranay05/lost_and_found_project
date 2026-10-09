/* Global Application Helper JS */

// Dynamic Backend API Base URL configuration
const IS_LOCAL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
const RENDER_BACKEND_URL = window.RENDER_BACKEND_URL || 'https://lost-found-portal.onrender.com';
const API_BASE = IS_LOCAL ? '/api' : `${RENDER_BACKEND_URL}/api`;

// Current session user object
let currentUser = null;

// Initialize Page Header / Session State
async function initSession() {
  try {
    const headers = {};
    const storedUserStr = localStorage.getItem('currentUser');
    if (storedUserStr) {
      try {
        const storedUser = JSON.parse(storedUserStr);
        if (storedUser && storedUser.id) headers['X-User-Id'] = String(storedUser.id);
        if (storedUser && storedUser.email) headers['X-User-Email'] = storedUser.email;
      } catch (e) {}
    }

    const res = await fetch(`${API_BASE}/auth/me`, { credentials: 'include', headers });
    if (res.ok) {
      currentUser = await res.json();
      localStorage.setItem('currentUser', JSON.stringify(currentUser));
      updateNavbar(currentUser);
      return currentUser;
    } else {
      currentUser = null;
      localStorage.removeItem('currentUser');
      updateNavbar(null);
      return null;
    }
  } catch (err) {
    console.warn('Session check failed:', err);
    updateNavbar(null);
    return null;
  }
}

// Mandatory Authentication Guard for protected pages
async function requireAuth(requiredRole = null) {
  const user = await initSession();
  if (!user) {
    const currentPath = window.location.pathname.split('/').pop() || 'index.html';
    showToast('Please login or register to perform this action.', 'error');
    setTimeout(() => {
      window.location.href = `login.html?redirect=${encodeURIComponent(currentPath)}`;
    }, 600);
    return null;
  }

  if (requiredRole && user.role !== requiredRole) {
    showToast(`Access Denied: ${requiredRole} permissions required.`, 'error');
    setTimeout(() => {
      window.location.href = user.role === 'ADMIN' ? 'admin-dashboard.html' : 'user-dashboard.html';
    }, 600);
    return null;
  }

  return user;
}

// Update Top Navigation Bar dynamically
function updateNavbar(user) {
  const userArea = document.getElementById('user-nav-area');
  const navLinks = document.getElementById('nav-links-area');
  
  if (!userArea) return;

  if (user) {
    const roleBadgeClass = user.role === 'ADMIN' ? 'role-pill admin' : 'role-pill user';
    userArea.innerHTML = `
      <div class="user-badge">
        <span>👤 ${escapeHtml(user.fullName)}</span>
        <span class="${roleBadgeClass}">${user.role}</span>
        <button onclick="handleLogout()" class="btn btn-sm btn-secondary" style="margin-left:0.5rem;">Logout</button>
      </div>
    `;

    if (navLinks) {
      let linksHtml = `
        <a href="index.html" class="nav-link">Home</a>
        <a href="search.html" class="nav-link">Search</a>
      `;

      if (user.role === 'ADMIN') {
        linksHtml += `
          <a href="admin-dashboard.html" class="nav-link ${window.location.pathname.includes('admin') ? 'active' : ''}">Admin Dashboard</a>
          <a href="qr-verification.html" class="nav-link ${window.location.pathname.includes('qr-verif') ? 'active' : ''}">Scan QR Code</a>
          <a href="transaction-history.html" class="nav-link ${window.location.pathname.includes('transaction') ? 'active' : ''}">Audit Log</a>
        `;
      } else {
        linksHtml += `
          <a href="user-dashboard.html" class="nav-link ${window.location.pathname.includes('user-dash') ? 'active' : ''}">Dashboard</a>
          <a href="report-lost.html" class="nav-link">Report Lost</a>
          <a href="register-found.html" class="nav-link">Register Found</a>
          <a href="claim-status.html" class="nav-link">My Claims</a>
        `;
      }
      navLinks.innerHTML = linksHtml;
    }
  } else {
    userArea.innerHTML = `
      <div style="display:flex; gap:0.5rem;">
        <a href="login.html" class="btn btn-sm btn-secondary">Login</a>
        <a href="register.html" class="btn btn-sm btn-primary">Register</a>
      </div>
    `;

    if (navLinks) {
      navLinks.innerHTML = `
        <a href="index.html" class="nav-link">Home</a>
        <a href="search.html" class="nav-link">Search Listings</a>
      `;
    }
  }
}

// Handle Logout
async function handleLogout() {
  try {
    await fetch(`${API_BASE}/auth/logout`, { method: 'POST', credentials: 'include' });
  } catch (err) {}
  localStorage.removeItem('currentUser');
  currentUser = null;
  showToast('Logged out successfully', 'success');
  setTimeout(() => {
    window.location.href = 'login.html';
  }, 500);
}

// Fetch API Wrapper
async function fetchApi(endpoint, options = {}) {
  const defaultHeaders = {
    'Content-Type': 'application/json'
  };

  const storedUserStr = localStorage.getItem('currentUser');
  if (storedUserStr) {
    try {
      const storedUser = JSON.parse(storedUserStr);
      if (storedUser && storedUser.id) defaultHeaders['X-User-Id'] = String(storedUser.id);
      if (storedUser && storedUser.email) defaultHeaders['X-User-Email'] = storedUser.email;
    } catch (e) {}
  }

  const config = {
    credentials: 'include',
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers
    }
  };

  try {
    const res = await fetch(`${API_BASE}${endpoint}`, config);
    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.message || 'API request failed');
    }

    if (data && data.user) {
      localStorage.setItem('currentUser', JSON.stringify(data.user));
      currentUser = data.user;
    }
    return data;
  } catch (err) {
    showToast(err.message, 'error');
    throw err;
  }
}

// Toast Notifications
function showToast(message, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✅' : type === 'error' ? '⚠️' : 'ℹ️'}</span>
    <div>${escapeHtml(message)}</div>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Helper: Get Badge HTML for Status
function getStatusBadge(status) {
  const s = status ? status.toUpperCase() : 'REPORTED';
  let badgeClass = 'badge-reported';
  if (s === 'MATCHED') badgeClass = 'badge-matched';
  if (s === 'CLAIMED') badgeClass = 'badge-claimed';
  if (s === 'RETURNED') badgeClass = 'badge-returned';
  if (s === 'REJECTED') badgeClass = 'badge-rejected';

  return `<span class="badge ${badgeClass}">${s}</span>`;
}

// Helper: Escape HTML string
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Helper: Copy text to clipboard with toast notification
function copyToClipboard(text, successMsg = 'Copied to clipboard!') {
  if (!text) return;
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(text).then(() => {
      showToast(successMsg, 'success');
    }).catch(() => {
      fallbackCopyText(text, successMsg);
    });
  } else {
    fallbackCopyText(text, successMsg);
  }
}

function fallbackCopyText(text, successMsg) {
  const textarea = document.createElement('textarea');
  textarea.value = text;
  document.body.appendChild(textarea);
  textarea.select();
  try {
    document.execCommand('copy');
    showToast(successMsg, 'success');
  } catch (err) {
    showToast('Failed to copy', 'error');
  }
  document.body.removeChild(textarea);
}

document.addEventListener('DOMContentLoaded', () => {
  initSession();
});
