import React from 'react';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { tomorrow } from 'react-syntax-highlighter/dist/esm/styles/prism';

interface SyntaxHighlighterProps {
  children: string;
  language?: string;
}

const SyntaxHighlighterComponent: React.FC<SyntaxHighlighterProps> = ({ 
  children, 
  language 
}) => {
  // Auto-detect language if not provided
  const detectLanguage = (code: string): string => {
    if (language) return language;
    
    // Simple language detection based on common patterns
    const firstLine = code.trim().split('\n')[0].toLowerCase();
    
    if (firstLine.includes('import') || firstLine.includes('export') || firstLine.includes('function') || firstLine.includes('const') || firstLine.includes('let') || firstLine.includes('var')) {
      return 'javascript';
    }
    if (firstLine.includes('import') && firstLine.includes('react')) {
      return 'jsx';
    }
    if (firstLine.includes('import') && firstLine.includes('from')) {
      return 'typescript';
    }
    if (firstLine.includes('import') && firstLine.includes('from') && firstLine.includes('react')) {
      return 'tsx';
    }
    if (firstLine.includes('def ') || firstLine.includes('import ') || firstLine.includes('class ') || firstLine.includes('print(')) {
      return 'python';
    }
    if (firstLine.includes('public class') || firstLine.includes('private ') || firstLine.includes('public ') || firstLine.includes('System.out')) {
      return 'java';
    }
    if (firstLine.includes('package ') || firstLine.includes('import ') && firstLine.includes('java')) {
      return 'java';
    }
    if (firstLine.includes('<?php') || firstLine.includes('$')) {
      return 'php';
    }
    if (firstLine.includes('<!DOCTYPE') || firstLine.includes('<html') || firstLine.includes('<div') || firstLine.includes('<p>')) {
      return 'html';
    }
    if (firstLine.includes('{') || firstLine.includes('color:') || firstLine.includes('background:')) {
      return 'css';
    }
    if (firstLine.includes('SELECT') || firstLine.includes('INSERT') || firstLine.includes('UPDATE') || firstLine.includes('DELETE')) {
      return 'sql';
    }
    if (firstLine.includes('dockerfile') || firstLine.includes('FROM ') || firstLine.includes('RUN ')) {
      return 'dockerfile';
    }
    if (firstLine.includes('yaml') || firstLine.includes('version:') || firstLine.includes('- ')) {
      return 'yaml';
    }
    if (firstLine.includes('json') || firstLine.includes('{') && firstLine.includes('"')) {
      return 'json';
    }
    if (firstLine.includes('bash') || firstLine.includes('#!/') || firstLine.includes('npm ') || firstLine.includes('git ')) {
      return 'bash';
    }
    
    return 'text'; // fallback
  };

  const detectedLanguage = detectLanguage(children);

  return (
    <SyntaxHighlighter
      language={detectedLanguage}
      style={tomorrow}
      customStyle={{
        margin: 0,
        borderRadius: '0.75rem',
        fontSize: '0.875rem',
        lineHeight: '1.5',
        background: '#f8fafc',
        border: 'none',
        boxShadow: '0 2px 8px rgba(0,0,0,0.12)',
      }}
      showLineNumbers={true}
      wrapLines={true}
      lineNumberStyle={{
        color: '#64748b',
        fontSize: '0.75rem',
        paddingRight: '1rem',
        minWidth: '2.5rem',
      }}
    >
      {children}
    </SyntaxHighlighter>
  );
};

export default SyntaxHighlighterComponent; 