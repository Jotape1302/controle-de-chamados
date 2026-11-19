import { LoginResponse, User } from '../types';

const STORAGE_KEY = 'homeney.session';

export interface Session {
  accessToken: string;
  expiresAt: string;
  user: User;
}

type Listener = () => void;
const unauthorizedListeners = new Set<Listener>();

function safeStorage(): Storage | null {
  try {
    return window.localStorage;
  } catch {
    return null;
  }
}

export const auth = {
  save(response: LoginResponse): Session {
    const session: Session = {
      accessToken: response.accessToken,
      expiresAt: response.expiresAt,
      user: response.user,
    };
    safeStorage()?.setItem(STORAGE_KEY, JSON.stringify(session));
    return session;
  },

  /** Retorna a sessão salva, descartando-a se estiver corrompida ou expirada. */
  load(): Session | null {
    const raw = safeStorage()?.getItem(STORAGE_KEY);
    if (!raw) return null;
    try {
      const session = JSON.parse(raw) as Session;
      if (!session.accessToken || new Date(session.expiresAt).getTime() <= Date.now()) {
        auth.clear();
        return null;
      }
      return session;
    } catch {
      auth.clear();
      return null;
    }
  },

  token(): string | null {
    return auth.load()?.accessToken ?? null;
  },

  clear(): void {
    safeStorage()?.removeItem(STORAGE_KEY);
  },

  /** Notifica a aplicação que o token foi rejeitado pela API (401). */
  notifyUnauthorized(): void {
    auth.clear();
    unauthorizedListeners.forEach((listener) => listener());
  },

  onUnauthorized(listener: Listener): () => void {
    unauthorizedListeners.add(listener);
    return () => unauthorizedListeners.delete(listener);
  },
};
