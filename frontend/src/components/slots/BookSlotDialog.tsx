import { format, parseISO } from "date-fns";
import { Loader2 } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import type { DoctorResponse, SlotResponse } from "@/types/api";

export function BookSlotDialog({
  open,
  onOpenChange,
  doctor,
  slot,
  onConfirm,
  pending,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  doctor: DoctorResponse;
  slot: SlotResponse | null;
  onConfirm: () => void;
  pending: boolean;
}) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Confirm booking</DialogTitle>
          <DialogDescription>
            Review the appointment details before confirming. A video room will be reserved for this slot.
          </DialogDescription>
        </DialogHeader>
        {slot ? (
          <div className="rounded-lg border bg-muted/40 p-4 text-sm">
            <p>
              <span className="text-muted-foreground">Doctor: </span>
              <strong>{doctor.name}</strong>
            </p>
            <p className="mt-1">
              <span className="text-muted-foreground">When: </span>
              <strong>{format(parseISO(slot.time), "EEE, d MMM yyyy 'at' HH:mm")}</strong>
            </p>
          </div>
        ) : null}
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={pending}>
            Cancel
          </Button>
          <Button onClick={onConfirm} disabled={pending || !slot}>
            {pending ? <Loader2 className="h-4 w-4 animate-spin" /> : null}
            Confirm booking
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
