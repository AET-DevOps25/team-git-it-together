import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { AlertTriangle } from 'lucide-react';
import React, { useState } from 'react';

const CONFIRM_PHRASE = 'I confirm the deletion of my account';

interface ConfirmDeletionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => void;
}

export function ConfirmDeletionDialog({
  open,
  onOpenChange,
  onConfirm,
}: ConfirmDeletionDialogProps) {
  const [deleteConfirmInput, setDeleteConfirmInput] = useState('');

  // Reset input when dialog opens/closes
  // (Prevents stale input if user opens twice)
  React.useEffect(() => {
    if (!open) setDeleteConfirmInput('');
  }, [open]);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-red-600">
            <AlertTriangle className="h-5 w-5" /> Confirm Account Deletion
          </DialogTitle>
        </DialogHeader>
        <div className="space-y-2">
          <p className="text-sm text-gray-700">
            This action cannot be undone. To permanently delete your account, please type:
          </p>
          <p className="rounded border bg-gray-100 px-2 py-1 text-center font-mono text-gray-800">
            {CONFIRM_PHRASE}
          </p>
          <Input
            value={deleteConfirmInput}
            onChange={(e) => setDeleteConfirmInput(e.target.value)}
            placeholder="Type the confirmation phrase exactly"
            className="w-full"
            autoFocus
          />
        </div>
        <DialogFooter className="flex flex-col gap-2 sm:flex-row">
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button
            variant="destructive"
            disabled={deleteConfirmInput !== CONFIRM_PHRASE}
            onClick={() => {
              onOpenChange(false);
              setDeleteConfirmInput('');
              onConfirm();
            }}
          >
            Delete
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
