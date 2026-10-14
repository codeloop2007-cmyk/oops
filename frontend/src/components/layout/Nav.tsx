import { Link, NavLink, useNavigate } from "react-router-dom";
import { LogOut, Moon, Stethoscope, Sun } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useSessionStore } from "@/store/session";
import { useEffect, useState } from "react";

function useTheme() {
  const [dark, setDark] = useState(() => document.documentElement.classList.contains("dark"));

  useEffect(() => {
    const stored = localStorage.getItem("mediconnect.theme");
    if (stored === "dark") {
      document.documentElement.classList.add("dark");
      setDark(true);
    }
  }, []);

  const toggle = () => {
    const next = !dark;
    setDark(next);
    document.documentElement.classList.toggle("dark", next);
    localStorage.setItem("mediconnect.theme", next ? "dark" : "light");
  };

  return { dark, toggle };
}

export function Nav() {
  const session = useSessionStore((s) => s.session);
  const clearSession = useSessionStore((s) => s.clearSession);
  const navigate = useNavigate();
  const { dark, toggle } = useTheme();

  const home = session?.role === "doctor" ? "/dashboard" : "/home";
  const name = session?.role === "patient" ? session.userName : session?.role === "doctor" ? session.doctorName : "";

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `rounded-md px-3 py-2 text-sm font-medium transition-colors ${
      isActive ? "bg-secondary text-secondary-foreground" : "text-muted-foreground hover:bg-accent hover:text-accent-foreground"
    }`;

  return (
    <header className="sticky top-0 z-40 border-b bg-background/90 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between gap-4 px-4">
        <Link to={home} className="flex items-center gap-2 font-semibold tracking-tight">
          <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <Stethoscope className="h-4 w-4" aria-hidden />
          </span>
          MediConnect
        </Link>

        <nav className="flex items-center gap-1" aria-label="Primary">
          {session?.role === "patient" ? (
            <>
              <NavLink to="/home" className={linkClass}>
                Home
              </NavLink>
              <NavLink to="/bookings" className={linkClass}>
                My Bookings
              </NavLink>
            </>
          ) : null}
          {session?.role === "doctor" ? (
            <>
              <NavLink to="/dashboard" end className={linkClass}>
                Dashboard
              </NavLink>
              <NavLink to="/dashboard/slots" className={linkClass}>
                Slots
              </NavLink>
            </>
          ) : null}
        </nav>

        <div className="flex items-center gap-2">
          <Button variant="ghost" size="icon" onClick={toggle} aria-label={dark ? "Switch to light mode" : "Switch to dark mode"}>
            {dark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
          </Button>
          {session ? (
            <>
              <span className="hidden max-w-[10rem] truncate text-sm sm:inline">{name}</span>
              <Badge variant="secondary">{session.role}</Badge>
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  clearSession();
                  navigate("/");
                }}
              >
                <LogOut className="h-4 w-4" />
                Logout
              </Button>
            </>
          ) : null}
        </div>
      </div>
    </header>
  );
}
