import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const ACTIONS = [
  { to: '/rides/new', title: 'Proposer un trajet', desc: 'Publiez un trajet en tant que conducteur.' },
  { to: '/rides/search', title: 'Rechercher un trajet', desc: 'Trouvez un trajet compatible.' },
  { to: '/dashboard/rides', title: 'Mes trajets', desc: 'Gerez les trajets que vous proposez.' },
  { to: '/dashboard/bookings', title: 'Mes reservations', desc: 'Suivez vos reservations.' },
  { to: '/dashboard/payments', title: 'Mes paiements', desc: 'Consultez votre historique de paiements.' },
  { to: '/notifications', title: 'Notifications', desc: 'Vos dernieres notifications.' },
  { to: '/profile', title: 'Mon profil', desc: 'Modifiez vos informations.' }
]

export default function DashboardPage() {
  const { user } = useAuth()

  return (
    <div className="space-y-8">
      <div className="rounded-2xl bg-gradient-to-br from-brand-600 to-brand-700 p-8 text-white">
        <h1 className="text-2xl font-bold">Bonjour {user?.firstName} 👋</h1>
        <p className="mt-1 text-brand-100">Que souhaitez-vous faire aujourd'hui ?</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {ACTIONS.map((a) => (
          <Link key={a.to} to={a.to} className="card transition hover:shadow-md hover:ring-brand-200">
            <h3 className="font-semibold text-slate-900">{a.title}</h3>
            <p className="mt-1 text-sm text-slate-500">{a.desc}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}
