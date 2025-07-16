import React from 'react';
import SyntaxHighlighterComponent from './SyntaxHighlighter';

interface MarkdownCodeBlockProps {
  children: React.ReactNode;
  className?: string;
}

const MarkdownCodeBlock: React.FC<MarkdownCodeBlockProps> = ({ children, className }) => {
  // Extract language from className (format: "language-{lang}")
  const language = className?.replace('language-', '') || undefined;
  
  // Convert children to string
  const codeString = React.Children.toArray(children).join('');
  
  return (
    <SyntaxHighlighterComponent language={language}>
      {codeString}
    </SyntaxHighlighterComponent>
  );
};

export default MarkdownCodeBlock; 