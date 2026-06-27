import { Routes, Route } from 'react-router-dom'
import Layout from './components/layout/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import HomePage from './pages/HomePage'
import NotFound from './pages/NotFound'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './features/auth/LoginPage'
import RegisterPage from './features/auth/RegisterPage'
import ProfilePage from './features/profile/ProfilePage'
import SearchRidesPage from './features/rides/SearchRidesPage'
import RideDetailsPage from './features/rides/RideDetailsPage'
import CreateRidePage from './features/rides/CreateRidePage'
import MyRidesPage from './features/rides/MyRidesPage'
import MyBookingsPage from './features/bookings/MyBookingsPage'
import RideBookingsPage from './features/bookings/RideBookingsPage'
import CheckoutPage from './features/payment/CheckoutPage'
import MyPaymentsPage from './features/payment/MyPaymentsPage'
import NotificationsPage from './features/notifications/NotificationsPage'
import AdminPage from './features/admin/AdminPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Public */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/rides/search" element={<SearchRidesPage />} />
        <Route path="/rides/:id" element={<RideDetailsPage />} />

        {/* Authentifie */}
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/dashboard/rides" element={<MyRidesPage />} />
          <Route path="/dashboard/bookings" element={<MyBookingsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/rides/new" element={<CreateRidePage />} />
          <Route path="/rides/:id/bookings" element={<RideBookingsPage />} />
          <Route path="/payment/:bookingId" element={<CheckoutPage />} />
          <Route path="/dashboard/payments" element={<MyPaymentsPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
        </Route>

        {/* Reserve aux administrateurs */}
        <Route element={<ProtectedRoute adminOnly />}>
          <Route path="/admin" element={<AdminPage />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}
