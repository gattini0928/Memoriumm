
import { useEffect, useState } from "react";

const API_URL = import.meta.env.VITE_API_URL;

const DEFAULT_USER_ID = "30d63a25-96e7-448c-b8b9-73d42956d755";

function App() {
  const params = new URLSearchParams(window.location.search);
  const userId = params.get("id") || DEFAULT_USER_ID;

  const [user, setUser] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch(`${API_URL}/users/${userId}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Erro HTTP ${response.status}`);
        }

        return response.json();
      })
      .then((data) => setUser(data))
      .catch((err) => setError(err.message));
  }, [userId]);

  if (error) return <p>Erro: {error}</p>;
  if (!user) return <p>Carregando perfil...</p>;

  return (
    <main>
      <h1>Memoriumm 🍂</h1>

      <section>
        <h2>{user.name}</h2>
        <p>@{user.username}</p>

        <p>{user.bio || "Nenhuma biografia ainda."}</p>

        <div>
          <span>{user.followersCount} seguidores</span>
          {" · "}
          <span>{user.followingCount} seguindo</span>
        </div>
      </section>
    </main>
  );
}

export default App;
