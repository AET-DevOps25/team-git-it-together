import React from 'react';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Tooltip, TooltipTrigger, TooltipContent } from '@/components/ui/tooltip';
import { X, Plus } from 'lucide-react';
import { CategoryPayload } from '@/types';

// Component
export function EditableInterests({
  allCategories,
  selected,
  onChange,
}: {
  allCategories: CategoryPayload[];
  selected: CategoryPayload[];
  onChange: (newInterests: CategoryPayload[]) => void;
}) {
  // IDs of current interests for easy checking
  const selectedIds = new Set(selected.map((c) => c.id));
  // The rest, not selected
  const available = allCategories.filter((c) => !selectedIds.has(c.id));

  return (
    <div className="flex flex-col gap-4">
      <div>
        <div className="mb-1 text-xs font-semibold text-gray-500">Your Interests</div>
        <div className="flex flex-wrap gap-2">
          {selected.length === 0 && (
            <span className="text-xs text-gray-400">No interests selected</span>
          )}
          {selected.map((interest) => (
            <Tooltip key={interest.id}>
              <TooltipTrigger asChild>
                <div className="flex items-center">
                  <Badge variant="secondary" className="flex items-center gap-1 pr-1">
                    <span>{interest.name}</span>
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      className="ml-1 h-4 w-4 p-0 text-gray-400 hover:text-red-500"
                      aria-label={`Remove ${interest.name}`}
                      onClick={() => onChange(selected.filter((c) => c.id !== interest.id))}
                    >
                      <X className="h-3 w-3" />
                    </Button>
                  </Badge>
                </div>
              </TooltipTrigger>
              {interest.description && <TooltipContent>{interest.description}</TooltipContent>}
            </Tooltip>
          ))}
        </div>
      </div>
      <div>
        <div className="mb-1 text-xs font-semibold text-gray-500">Other Categories</div>
        <div className="flex flex-wrap gap-2">
          {available.length === 0 && (
            <span className="text-xs text-gray-400">No more categories</span>
          )}
          {available.map((category) => (
            <Tooltip key={category.id}>
              <TooltipTrigger asChild>
                <div className="flex items-center">
                  <Badge
                    variant="outline"
                    className="flex cursor-pointer items-center gap-1 pr-1"
                    onClick={() => onChange([...selected, category])}
                  >
                    <span>{category.name}</span>
                    <Plus className="ml-1 h-3 w-3 text-blue-600" />
                  </Badge>
                </div>
              </TooltipTrigger>
              {category.description && <TooltipContent>{category.description}</TooltipContent>}
            </Tooltip>
          ))}
        </div>
      </div>
    </div>
  );
}
