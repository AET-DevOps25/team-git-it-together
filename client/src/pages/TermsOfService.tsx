import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { FileText, CheckCircle, AlertCircle, Shield, Users } from 'lucide-react';

const TermsOfService = () => {
  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      
      <div className="mx-auto max-w-4xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <div className="flex justify-center mb-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-r from-green-100 to-blue-100">
              <FileText className="h-8 w-8 text-green-600" />
            </div>
          </div>
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Terms of Service
          </h1>
          <p className="text-xl text-gray-600">
            Last updated: July 2025
          </p>
        </div>

        <div className="prose prose-lg max-w-none">
          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">1. Acceptance of Terms</h2>
            <p className="text-gray-600">
              By accessing and using SkillForge ("the Platform"), you accept and agree to be bound by these Terms of Service ("Terms"). 
              If you do not agree to these Terms, please do not use our service. These Terms constitute a legally binding agreement 
              between you and SkillForge.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">2. Description of Service</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                SkillForge is an AI-powered learning platform that provides:
              </p>
              <ul className="space-y-2 ml-6">
                <li>• AI-curated educational courses and learning paths</li>
                <li>• Personalized learning experiences and recommendations</li>
                <li>• Progress tracking and achievement systems</li>
                <li>• Interactive AI chat assistant for learning support</li>
                <li>• Course bookmarking and organization tools</li>
                <li>• Community features and collaboration opportunities</li>
              </ul>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">3. User Accounts and Registration</h2>
            <div className="space-y-4 text-gray-600">
              <div className="flex items-start space-x-3">
                <CheckCircle className="h-5 w-5 text-green-600 mt-1" />
                <div>
                  <h3 className="font-semibold text-gray-900">Account Creation</h3>
                  <p>You must create an account to access certain features. You must provide accurate, current, and complete information during registration and keep your account information updated.</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <AlertCircle className="h-5 w-5 text-orange-600 mt-1" />
                <div>
                  <h3 className="font-semibold text-gray-900">Account Security</h3>
                  <p>You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account. Notify us immediately of any unauthorized use.</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <Users className="h-5 w-5 text-blue-600 mt-1" />
                <div>
                  <h3 className="font-semibold text-gray-900">Age Requirements</h3>
                  <p>You must be at least 13 years old to use our service. If you are under 18, you must have parental or guardian consent to use our platform.</p>
                </div>
              </div>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">4. Acceptable Use Policy</h2>
            <div className="space-y-4 text-gray-600">
              <p>You agree not to use the service to:</p>
              <ul className="space-y-2 ml-6">
                <li>• Violate any applicable laws, regulations, or third-party rights</li>
                <li>• Infringe on intellectual property rights or privacy rights</li>
                <li>• Harass, abuse, threaten, or harm other users or individuals</li>
                <li>• Attempt to gain unauthorized access to our systems or other users' accounts</li>
                <li>• Interfere with the proper functioning of the platform or its security measures</li>
                <li>• Share inappropriate, offensive, or harmful content</li>
                <li>• Use automated systems to access the service without permission</li>
                <li>• Reverse engineer, decompile, or attempt to extract source code</li>
              </ul>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">5. Intellectual Property Rights</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                The Platform and its original content, features, functionality, and design are owned by SkillForge and are protected by international copyright, trademark, patent, trade secret, and other intellectual property laws.
              </p>
              <p>
                Course content is provided by various creators and is subject to their respective licenses and terms. You retain ownership of any content you create, but grant us a license to use it for platform functionality.
              </p>
              <p>
                You may not reproduce, distribute, modify, or create derivative works of our content without explicit permission.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">6. Privacy and Data Protection</h2>
            <div className="flex items-start space-x-3 mb-4">
              <Shield className="h-5 w-5 text-blue-600 mt-1" />
              <div>
                <p className="text-gray-600">
                  Your privacy is important to us. Our collection, use, and protection of your personal information is governed by our Privacy Policy, which is incorporated into these Terms by reference.
                </p>
              </div>
            </div>
            <div className="space-y-3 text-gray-600">
              <p><strong>Data Processing:</strong> We process your data in accordance with applicable data protection laws, including GDPR and CCPA.</p>
              <p><strong>Data Security:</strong> We implement appropriate technical and organizational measures to protect your personal data.</p>
              <p><strong>Data Retention:</strong> We retain your data only as long as necessary to provide our services and comply with legal obligations.</p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">7. Service Availability and Modifications</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                We strive to provide reliable service but cannot guarantee uninterrupted access. We may modify, suspend, or discontinue any part of the service with reasonable notice.
              </p>
              <p>
                We reserve the right to update these Terms at any time. Material changes will be communicated through the platform or email with at least 30 days' notice.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">8. Limitation of Liability</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                To the maximum extent permitted by law, SkillForge shall not be liable for any indirect, incidental, special, consequential, or punitive damages, including without limitation, loss of profits, data, use, goodwill, or other intangible losses.
              </p>
              <p>
                Our total liability to you for any claims arising from the use of our service shall not exceed the amount you paid us in the twelve months preceding the claim, or $100, whichever is greater.
              </p>
              <p>
                Some jurisdictions do not allow the exclusion of certain warranties or limitations of liability, so some of these limitations may not apply to you.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">9. Indemnification</h2>
            <p className="text-gray-600">
              You agree to indemnify and hold harmless SkillForge, its officers, directors, employees, and agents from any claims, damages, losses, or expenses arising from your use of the service or violation of these Terms.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">10. Termination</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                We may terminate or suspend your account immediately, without prior notice, for any violation of these Terms or for any other reason at our sole discretion.
              </p>
              <p>
                Upon termination, your right to use the service will cease immediately. You may terminate your account at any time by contacting us or discontinuing use of the service.
              </p>
              <p>
                Provisions of these Terms that by their nature should survive termination shall remain in effect after termination.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">11. Governing Law and Dispute Resolution</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                These Terms are governed by and construed in accordance with the laws of the jurisdiction where SkillForge is incorporated, without regard to conflict of law principles.
              </p>
              <p>
                Any disputes arising from these Terms or your use of the service shall be resolved through binding arbitration in accordance with the rules of the American Arbitration Association.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">12. Severability</h2>
            <p className="text-gray-600">
              If any provision of these Terms is found to be unenforceable or invalid, that provision will be limited or eliminated to the minimum extent necessary so that these Terms will otherwise remain in full force and effect.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">13. Contact Information</h2>
            <p className="text-gray-600">
              If you have any questions about these Terms of Service, please contact us at legal@skillforge.ai or through our platform's support system.
            </p>
          </section>
        </div>

        <div className="mt-12 text-center">
          <Link to="/">
            <Button variant="outline" size="lg">
              Back to Home
            </Button>
          </Link>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default TermsOfService; 