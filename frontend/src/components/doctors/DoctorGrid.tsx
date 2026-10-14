import { DoctorCard } from "@/components/doctors/DoctorCard";
import type { DoctorResponse } from "@/types/api";

export function DoctorGrid({
  doctors,
  aiMatch = false,
}: {
  doctors: DoctorResponse[];
  aiMatch?: boolean;
}) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {doctors.map((doctor) => (
        <DoctorCard key={doctor.doctorId} doctor={doctor} aiMatch={aiMatch} />
      ))}
    </div>
  );
}
