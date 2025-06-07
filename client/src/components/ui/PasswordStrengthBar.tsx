import zxcvbn from 'zxcvbn';

const strengthColors = [
  'bg-red-500', // 0 - Weak
  'bg-orange-500', // 1 - Weak
  'bg-yellow-500', // 2 - Medium
  'bg-blue-500', // 3 - Good
  'bg-green-500', // 4 - Strong
];

const strengthLabels = ['Very Weak', 'Weak', 'Medium', 'Good', 'Strong'];

export function PasswordStrengthBar({ password }: { password: string }) {
  const { score, feedback } = zxcvbn(password || '');

  return (
    <div className="mt-2">
      <div className="mb-1 h-2 w-full rounded bg-gray-200">
        <div
          className={`h-2 rounded transition-all duration-300 ${strengthColors[score]}`}
          style={{
            width: `${(score + 1) * 20}%`,
          }}
        />
      </div>
      <div className="flex items-center justify-between text-xs">
        <span
          className={`font-medium ${score >= 3 ? 'text-green-700' : score === 2 ? 'text-yellow-700' : 'text-red-700'}`}
        >
          {strengthLabels[score]}
        </span>
        {feedback.warning && <span className="text-gray-400">{feedback.warning}</span>}
      </div>
      {feedback.suggestions.length > 0 && (
        <ul className="mt-1 list-disc pl-4 text-xs text-gray-500">
          {feedback.suggestions.map((s, idx) => (
            <li key={idx}>{s}</li>
          ))}
        </ul>
      )}
    </div>
  );
}
