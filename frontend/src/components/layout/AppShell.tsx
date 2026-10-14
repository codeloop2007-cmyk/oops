import { Outlet } from "react-router-dom";
import { Nav } from "@/components/layout/Nav";

export function AppShell() {
  return (
    <div className="min-h-svh bg-background">
      <Nav />
      <main className="mx-auto w-full max-w-6xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  );
}
