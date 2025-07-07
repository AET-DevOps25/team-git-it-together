import React from 'react';
import { Button } from '@/components/ui/button';
import { Award } from 'lucide-react';
import jsPDF from 'jspdf';

interface CertificateGeneratorProps {
  courseTitle: string;
  userFirstName: string;
  userLastName: string;
  completionDate: string;
  instructor: string;
}

const CertificateGenerator: React.FC<CertificateGeneratorProps> = ({
  courseTitle,
  userFirstName,
  userLastName,
  completionDate,
  instructor
}) => {
  const generateCertificate = () => {
    const doc = new jsPDF({
      orientation: 'landscape',
      unit: 'mm',
      format: 'a4'
    });

    // Set background color
    doc.setFillColor(248, 250, 252); // Light gray background
    doc.rect(0, 0, 297, 210, 'F');

    // Add decorative border
    doc.setDrawColor(59, 130, 246); // Blue border
    doc.setLineWidth(3);
    doc.rect(10, 10, 277, 190);

    // Add inner border
    doc.setLineWidth(1);
    doc.setDrawColor(203, 213, 225); // Light gray inner border
    doc.rect(15, 15, 267, 180);

    // Add certificate title
    doc.setFontSize(36);
    doc.setTextColor(59, 130, 246); // Blue text
    doc.setFont('helvetica', 'bold');
    doc.text('Certificate of Completion', 148.5, 50, { align: 'center' });

    // Add decorative line
    doc.setDrawColor(59, 130, 246);
    doc.setLineWidth(2);
    doc.line(60, 65, 237, 65);

    // Add main text
    doc.setFontSize(16);
    doc.setTextColor(51, 65, 85); // Dark gray text
    doc.setFont('helvetica', 'normal');
    doc.text('This is to certify that', 148.5, 90, { align: 'center' });

    // Add student name
    doc.setFontSize(24);
    doc.setTextColor(59, 130, 246);
    doc.setFont('helvetica', 'bold');
    const fullName = `${userFirstName} ${userLastName}`.trim();
    
    // Handle long names by wrapping text
    const maxNameWidth = 180;
    const nameLines = doc.splitTextToSize(fullName, maxNameWidth);
    const nameY = 110;
    
    nameLines.forEach((line: string, index: number) => {
      doc.text(line, 148.5, nameY + (index * 8), { align: 'center' });
    });

    // Add completion text
    doc.setFontSize(16);
    doc.setTextColor(51, 65, 85);
    doc.setFont('helvetica', 'normal');
    doc.text('has successfully completed the course', 148.5, 130 + (nameLines.length - 1) * 8, { align: 'center' });

    // Add course title
    doc.setFontSize(20);
    doc.setTextColor(59, 130, 246);
    doc.setFont('helvetica', 'bold');
    
    // Handle long course titles by wrapping text
    const maxWidth = 200;
    const courseTitleLines = doc.splitTextToSize(courseTitle, maxWidth);
    const courseTitleY = 150 + (nameLines.length - 1) * 8;
    
    courseTitleLines.forEach((line: string, index: number) => {
      doc.text(line, 148.5, courseTitleY + (index * 8), { align: 'center' });
    });

    // Add completion date
    doc.setFontSize(14);
    doc.setTextColor(51, 65, 85);
    doc.setFont('helvetica', 'normal');
    const dateY = 170 + (nameLines.length - 1) * 8 + (courseTitleLines.length - 1) * 8;
    doc.text(`Completed on: ${completionDate}`, 148.5, dateY, { align: 'center' });

    // Add instructor
    doc.text(`Instructor: ${instructor}`, 148.5, dateY + 15, { align: 'center' });

    // Add decorative elements
    doc.setDrawColor(59, 130, 246);
    doc.setLineWidth(1);
    
    // Top left corner decoration
    doc.line(20, 30, 40, 30);
    doc.line(20, 30, 20, 50);
    
    // Top right corner decoration
    doc.line(277, 30, 257, 30);
    doc.line(277, 30, 277, 50);
    
    // Bottom left corner decoration
    doc.line(20, 180, 40, 180);
    doc.line(20, 180, 20, 160);
    
    // Bottom right corner decoration
    doc.line(277, 180, 257, 180);
    doc.line(277, 180, 277, 160);

    // Add watermark
    doc.setFontSize(60);
    doc.setTextColor(248, 250, 252);
    doc.setFont('helvetica', 'bold');
    doc.text('COMPLETED', 148.5, 120, { align: 'center', angle: 45 });

    // Save the PDF
    const safeDate = completionDate.replace(/[^a-zA-Z0-9]/g, '_');
    const fileName = `SkillForge_Certificate_${courseTitle.replace(/[^a-zA-Z0-9]/g, '_')}_${fullName.replace(/[^a-zA-Z0-9]/g, '_')}_${safeDate}.pdf`;
    doc.save(fileName);
  };

  return (
    <Button
      onClick={generateCertificate}
      className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white" aria-label="Download course completion certificate">
      <Award className="mr-2 h-4 w-4" />
      Download Certificate
    </Button>
  );
};

export default CertificateGenerator; 