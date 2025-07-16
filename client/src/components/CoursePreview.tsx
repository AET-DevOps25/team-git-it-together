import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';

interface Lesson {
  id?: string;
  title: string;
  summary?: string;
  content?: any;
  description?: string;
  moduleTitle?: string;
  order?: number;
}

interface CoursePreviewProps {
  course: any; // Accept CoursePayload or CourseResponse
}

const CoursePreview: React.FC<CoursePreviewProps> = ({ course }) => {
  const [expandedLessons, setExpandedLessons] = useState<{ [key: string]: boolean }>({});
  const [expandedModules, setExpandedModules] = useState<{ [key: string]: boolean }>({});
  if (!course) return null;

  const title = course.title || course.name || 'Untitled Course';
  const description = course.description || '';
  const instructor = course.instructor || (course.instructorName ?? '');
  const level = course.level || '';
  const language = course.language || '';

  // Group lessons by module if modules exist
  let modules: any[] = [];
  if (Array.isArray(course.modules) && course.modules.length > 0) {
    modules = course.modules
      .filter((mod: any) => Array.isArray(mod.lessons))
      .sort((a: any, b: any) => (a.order ?? 0) - (b.order ?? 0));
  } else {
    // Fallback: treat all lessons as a single module
    modules = [{
      title: 'Lessons',
      lessons: Array.isArray(course.lessons) ? course.lessons : [],
      order: 1,
    }];
  }

  const toggleModule = (modIdx: number) => {
    setExpandedModules((prev) => ({ ...prev, [modIdx]: !prev[modIdx] }));
  };

  const toggleLesson = (modIdx: number, lessonIdx: number) => {
    const key = `${modIdx}-${lessonIdx}`;
    setExpandedLessons((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  return (
    <Card className="w-full max-w-2xl mx-auto shadow-lg border border-gray-200">
      <CardHeader>
        <CardTitle className="text-2xl font-bold text-blue-700 mb-1">{title}</CardTitle>
        <CardDescription className="text-gray-600 mb-2">{description}</CardDescription>
        <div className="flex flex-wrap gap-4 text-sm text-gray-500 mt-2">
          {instructor && <span><strong>Instructor:</strong> {instructor}</span>}
          {level && <span><strong>Level:</strong> {level}</span>}
          {language && <span><strong>Language:</strong> {language}</span>}
        </div>
      </CardHeader>
      <CardContent>
        <div className="mt-4">
          <h3 className="text-lg font-semibold mb-2 text-purple-700">Lessons</h3>
          {modules.length === 0 && <div className="text-gray-400 italic">No lessons available.</div>}
          <div className="space-y-6">
            {modules.map((mod, modIdx) => (
              <div key={mod.title + modIdx} className="border border-gray-200 rounded-lg">
                <div className="flex items-center justify-between bg-gray-100 px-4 py-2 cursor-pointer rounded-t-lg" onClick={() => toggleModule(modIdx)}>
                  <span className="font-semibold text-blue-700">
                    {mod.title}
                  </span>
                  <button className="text-xs text-blue-500 underline focus:outline-none" onClick={e => { e.stopPropagation(); toggleModule(modIdx); }}>
                    {expandedModules[modIdx] ? 'Hide Lessons' : 'Show Lessons'}
                  </button>
                </div>
                {expandedModules[modIdx] && (
                  <ol className="space-y-4 px-4 py-2">
                    {mod.lessons.map((lesson: Lesson, lessonIdx: number) => {
                      const key = `${modIdx}-${lessonIdx}`;
                      return (
                        <li key={lesson.id || lessonIdx} className="bg-gray-50 rounded-lg p-4 border border-gray-100">
                          <div className="font-semibold text-blue-600 mb-1 flex items-center justify-between">
                            <div>
                              {lessonIdx + 1}. {lesson.title}
                            </div>
                            {lesson.content && (
                              <button
                                className="ml-2 text-xs text-blue-500 underline focus:outline-none"
                                onClick={() => toggleLesson(modIdx, lessonIdx)}
                              >
                                {expandedLessons[key] ? 'Hide Content' : 'Show Content'}
                              </button>
                            )}
                          </div>
                          {lesson.summary && <div className="text-gray-700 mb-1">{lesson.summary}</div>}
                          {lesson.description && <div className="text-gray-700 mb-1">{lesson.description}</div>}
                          {lesson.content && expandedLessons[key] && (typeof lesson.content === 'string' ? (
                            <div className="text-sm text-gray-500 whitespace-pre-line mt-2">{lesson.content}</div>
                          ) : lesson.content.content ? (
                            <div className="text-sm text-gray-500 whitespace-pre-line mt-2">{lesson.content.content}</div>
                          ) : null)}
                        </li>
                      );
                    })}
                  </ol>
                )}
              </div>
            ))}
          </div>
        </div>
        {/* Actions (disabled) */}
        <div className="mt-8 flex gap-4">
          <button className="bg-gray-200 text-gray-500 px-4 py-2 rounded cursor-not-allowed" disabled>Enroll (Preview Only)</button>
          <button className="bg-gray-200 text-gray-500 px-4 py-2 rounded cursor-not-allowed" disabled>Bookmark (Preview Only)</button>
        </div>
      </CardContent>
    </Card>
  );
};

export default CoursePreview; 