import { Link } from "react-router-dom";
import { MapPin, Languages } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { avatarColor, formatFee, initials } from "@/lib/utils";
import type { DoctorResponse } from "@/types/api";

export function DoctorCard({
  doctor,
  aiMatch = false,
}: {
  doctor: DoctorResponse;
  aiMatch?: boolean;
}) {
  return (
    <Card className="h-full transition-shadow hover:shadow-md">
      <CardContent className="flex h-full flex-col gap-3 p-4">
        <div className="flex items-start gap-3">
          <div
            className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-full text-sm font-semibold text-white ${avatarColor(doctor.doctorId)}`}
            aria-hidden
          >
            {initials(doctor.name)}
          </div>
          <div className="min-w-0 flex-1">
            <div className="flex flex-wrap items-center gap-2">
              <h3 className="truncate font-semibold">{doctor.name}</h3>
              {aiMatch ? <Badge variant="warning">AI match</Badge> : null}
            </div>
            <Badge variant="secondary" className="mt-1">
              {doctor.specialization}
            </Badge>
          </div>
        </div>
        <div className="space-y-1 text-sm text-muted-foreground">
          {doctor.location ? (
            <p className="flex items-center gap-1.5">
              <MapPin className="h-3.5 w-3.5" aria-hidden />
              {doctor.location}
            </p>
          ) : null}
          {doctor.language ? (
            <p className="flex items-center gap-1.5">
              <Languages className="h-3.5 w-3.5" aria-hidden />
              {doctor.language}
            </p>
          ) : null}
        </div>
        <div className="mt-auto flex items-center justify-between pt-2">
          <p className="font-semibold text-primary">{formatFee(Number(doctor.consultationFee))}</p>
          <Button asChild size="sm">
            <Link to={`/doctors/${doctor.doctorId}`}>View Profile</Link>
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
