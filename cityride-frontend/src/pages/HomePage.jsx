import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const FEATURES = [
  { title: 'Trouvez le bon trajet', text: 'Un matching intelligent classe les trajets par compatibilite (lieu, horaire, prix).' },
  { title: 'Reservez en un clic', text: 'Choisissez votre place, reservez et payez en ligne en toute simplicite.' },
  { title: 'Voyagez en confiance', text: 'Profils verifies, notes et avis apres chaque trajet.' }
]

export default function HomePage() {
  const { isAuthenticated } = useAuth()

  return (
    <div className="space-y-20">
      {/* Hero */}
      <section className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-brand-600 to-brand-800 px-6 py-20 text-center text-white">
        <h1 className="mx-auto max-w-3xl text-4xl font-extrabold tracking-tight sm:text-5xl">
          Le covoiturage urbain, simple et malin
        </h1>
        <p className="mx-auto mt-5 max-w-xl text-lg text-brand-100">
          Partagez vos trajets en ville, economisez et reduisez votre empreinte carbone avec CityRide.
        </p>
        <div className="mt-8 flex flex-wrap justify-center gap-3">
          <Link to="/rides/search" className="btn bg-white text-brand-700 hover:bg-brand-50">
            Chercher un trajet
          </Link>
          <Link to={isAuthenticated ? '/rides/new' : '/register'} className="btn bg-brand-500 text-white hover:bg-brand-400">
            Proposer un trajet
          </Link>
        </div>
      </section>

      {/* Features */}
      <section className="grid gap-6 sm:grid-cols-3">
        {FEATURES.map((f) => (
          <div key={f.title} className="card">
            <h3 className="text-lg font-bold text-slate-900">{f.title}</h3>
            <p className="mt-2 text-sm text-slate-600">{f.text}</p>
          </div>
        ))}
      </section>

      {/* CTA */}
      <section className="card flex flex-col items-center gap-4 text-center">
        <h2 className="text-2xl font-bold text-slate-900">Pret a partager la route ?</h2>
        <p className="max-w-md text-slate-600">Rejoignez la communaute CityRide et commencez des aujourd'hui.</p>
        {!isAuthenticated && (
          <Link to="/register" className="btn-primary">Creer mon compte</Link>
        )}
      </section>
    </div>
  )
}
