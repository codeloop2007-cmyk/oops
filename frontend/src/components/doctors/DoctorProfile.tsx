import { MapPin, Languages, GraduationCap } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { avatarColor, formatFee, initials } from "@/lib/utils";
import type { DoctorResponse } from "@/types/api";

export function DoctorProfileHeader({ doctor }: { doctor: DoctorResponse }) {
  return (
    <Card>
      <CardContent className="space-y-4 p-6">
        <div className="flex items-start gap-4">
          <div
            className={`flex h-20 w-20 shrink-0 items-center justify-center rounded-full text-2xl font-semibold text-white ${avatarColor(doctor.doctorId)}`}
            aria-hidden
          >
            {initials(doctor.name)}
          </div>
          <div>
            <h1 className="text-2xl font-semibold">{doctor.name}</h1>
            <Badge className="mt-2">{doctor.specialization}</Badge>
          </div>
        </div>
        <dl className="space-y-2 text-sm">
          {doctor.qualification ? (
            <div className="flex items-start gap-2">
              <GraduationCap className="mt-0.5 h-4 w-4 text-muted-foreground" aria-hidden />
              <div>
                <dt className="text-muted-foreground">Qualification</dt>
                <dd>{doctor.qualification}</dd>
              </div>
            </div>
          ) : null}
          {doctor.language ? (
            <div className="flex items-start gap-2">
              <Languages className="mt-0.5 h-4 w-4 text-muted-foreground" aria-hidden />
              <div>
                <dt className="text-muted-foreground">Languages</dt>
                <dd>{doctor.language}</dd>
              </div>
            </div>
          ) : null}
          {doctor.location ? (
            <div className="flex items-start gap-2">
              <MapPin className="mt-0.5 h-4 w-4 text-muted-foreground" aria-hidden />
              <div>
                <dt className="text-muted-foreground">Location</dt>
                <dd>{doctor.location}</dd>
              </div>
            </div>
          ) : null}
        </dl>
        <div className="rounded-lg bg-primary/10 p-4">
          <p className="text-sm text-muted-foreground">Consultation fee</p>
          <p className="text-2xl font-semibold text-primary">{formatFee(Number(doctor.consultationFee))}</p>
        </div>
      </CardContent>
    </Card>
  );
}
