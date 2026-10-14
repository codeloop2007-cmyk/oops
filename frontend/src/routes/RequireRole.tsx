import { Navigate, Outlet } from "react-router-dom";
import { useSessionStore } from "@/store/session";

type Role = "patient" | "doctor";

export function RequireRole({ role }: { role: Role | Role[] }) {
  const session = useSessionStore((s) => s.session);
  const allowed = Array.isArray(role) ? role : [role];

  if (!session || !allowed.includes(session.role)) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
