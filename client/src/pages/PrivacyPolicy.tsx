import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { Shield, Lock, Eye, Database, User, Globe } from 'lucide-react';

const PrivacyPolicy = () => {
  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      
      <div className="mx-auto max-w-4xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <div className="flex justify-center mb-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-r from-blue-100 to-purple-100">
              <Shield className="h-8 w-8 text-blue-600" />
            </div>
          </div>
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Privacy Policy
          </h1>
          <p className="text-xl text-gray-600">
            Last updated: July 2025
          </p>
        </div>

        <div className="prose prose-lg max-w-none">
          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">1. Introduction</h2>
            <p className="text-gray-600">
              At SkillForge, we are committed to protecting your privacy and ensuring the security of your personal information. 
              This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our AI-powered 
              learning platform. By using our service, you consent to the data practices described in this policy.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">2. Information We Collect</h2>
            <div className="space-y-4 text-gray-600">
              <div className="flex items-start space-x-3">
                <User className="h-5 w-5 text-blue-600 mt-1" />
                <div>
                  <h3 className="font-semibold text-gray-900">Personal Information</h3>
                  <p>We collect information you provide directly to us, including:</p>
                  <ul className="mt-2 ml-6 space-y-1">
                    <li>Name, email address, and contact information</li>
                    <li>Account credentials and profile information</li>
                    <li>Learning preferences and educational background</li>
                    <li>Communication preferences and settings</li>
                  </ul>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <Database className="h-5 w-5 text-green-600 mt-1" />
                <div>
                  <h3 className="font-semibold text-gray-900">Usage Data</h3>
                  <p>We automatically collect information about your use of our platform:</p>
                  <ul className="mt-2 ml-6 space-y-1">
                    <li>Course progress, completion rates, and learning patterns</li>
                    <li>Interaction with AI features and chat assistant</li>
                    <li>Bookmarked courses and learning preferences</li>
                    <li>Achievement and progress tracking data</li>
                  </ul>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <Eye className="h-5 w-5 text-purple-600 mt-1" />
                <div>
                  <h3 className="font-semibold text-gray-900">Technical Information</h3>
                  <p>We collect technical information about your device and connection:</p>
                  <ul className="mt-2 ml-6 space-y-1">
                    <li>IP address, browser type, and operating system</li>
                    <li>Device identifiers and mobile network information</li>
                    <li>Usage analytics and performance metrics</li>
                    <li>Error logs and crash reports</li>
                  </ul>
                </div>
              </div>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">3. How We Use Your Information</h2>
            <div className="space-y-4 text-gray-600">
              <p>We use your information for the following purposes:</p>
              <ul className="space-y-2 ml-6">
                <li><strong>Service Provision:</strong> Provide and maintain our learning platform and features</li>
                <li><strong>Personalization:</strong> Create personalized learning experiences and recommendations</li>
                <li><strong>Progress Tracking:</strong> Monitor and track your learning progress and achievements</li>
                <li><strong>Communication:</strong> Send important updates, notifications, and support messages</li>
                <li><strong>Improvement:</strong> Analyze usage patterns to improve our services and develop new features</li>
                <li><strong>Security:</strong> Ensure platform security, prevent fraud, and protect user accounts</li>
                <li><strong>Compliance:</strong> Comply with legal obligations and enforce our terms of service</li>
              </ul>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">4. Legal Basis for Processing (GDPR)</h2>
            <div className="space-y-4 text-gray-600">
              <p>Under GDPR, we process your personal data based on the following legal grounds:</p>
              <ul className="space-y-2 ml-6">
                <li>• <strong>Consent:</strong> When you explicitly agree to our data processing activities</li>
                <li>• <strong>Contract Performance:</strong> To provide the services you've requested</li>
                <li>• <strong>Legitimate Interest:</strong> To improve our services and ensure security</li>
                <li>• <strong>Legal Obligation:</strong> To comply with applicable laws and regulations</li>
              </ul>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">5. Information Sharing and Disclosure</h2>
            <p className="text-gray-600 mb-4">
              We do not sell, trade, or rent your personal information to third parties. We may share your information in the following circumstances:
            </p>
            <div className="space-y-3 text-gray-600">
              <div className="flex items-start space-x-3">
                <Globe className="h-5 w-5 text-blue-600 mt-1" />
                <div>
                  <p><strong>Service Providers:</strong> We may share information with trusted third-party service providers who assist us in operating our platform, such as cloud hosting, analytics, and payment processing services.</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <Shield className="h-5 w-5 text-red-600 mt-1" />
                <div>
                  <p><strong>Legal Requirements:</strong> We may disclose information if required by law, court order, or government request, or to protect our rights, property, or safety.</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <Database className="h-5 w-5 text-green-600 mt-1" />
                <div>
                  <p><strong>Business Transfers:</strong> In the event of a merger, acquisition, or sale of assets, your information may be transferred as part of the business assets.</p>
                </div>
              </div>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">6. Data Security</h2>
            <div className="flex items-start space-x-3 mb-4">
              <Lock className="h-5 w-5 text-green-600 mt-1" />
              <div>
                <p className="text-gray-600">
                  We implement comprehensive security measures to protect your personal information against unauthorized access, alteration, disclosure, or destruction.
                </p>
              </div>
            </div>
            <ul className="space-y-2 text-gray-600">
              <li><strong>Encryption:</strong> All data is encrypted in transit (TLS/SSL) and at rest (AES-256)</li>
              <li><strong>Access Controls:</strong> Strict access controls and authentication measures</li>
              <li><strong>Regular Audits:</strong> Security assessments and vulnerability testing</li>
              <li><strong>Data Backup:</strong> Secure backup procedures and disaster recovery</li>
              <li><strong>Employee Training:</strong> Regular security training for all staff</li>
            </ul>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">7. Your Rights and Choices</h2>
            <div className="space-y-4 text-gray-600">
              <p>You have the following rights regarding your personal information:</p>
              <ul className="space-y-2 ml-6">
                <li><strong>Access:</strong> Request access to your personal information and receive a copy</li>
                <li><strong>Correction:</strong> Request correction of inaccurate or incomplete information</li>
                <li><strong>Deletion:</strong> Request deletion of your personal information (right to be forgotten)</li>
                <li><strong>Portability:</strong> Request a copy of your data in a structured, machine-readable format</li>
                <li><strong>Restriction:</strong> Request restriction of processing in certain circumstances</li>
                <li><strong>Objection:</strong> Object to processing based on legitimate interests</li>
                <li><strong>Withdrawal:</strong> Withdraw consent at any time where processing is based on consent</li>
              </ul>
              <p className="mt-4">
                To exercise these rights, please contact us at privacy@skillforge.ai. We will respond to your request within 30 days.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">8. Data Retention</h2>
            <div className="space-y-4 text-gray-600">
              <p>We retain your personal information only as long as necessary to:</p>
              <ul className="space-y-2 ml-6">
                <li>Provide our services and maintain your account</li>
                <li>Comply with legal obligations and regulatory requirements</li>
                <li>Resolve disputes and enforce our agreements</li>
                <li>Improve our services and develop new features</li>
              </ul>
              <p>
                When we no longer need your information, we will securely delete or anonymize it. Account data is typically retained for 3 years after account deletion, while usage analytics may be retained for up to 2 years.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">9. International Data Transfers</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                Your information may be transferred to and processed in countries other than your own. We ensure appropriate safeguards are in place for international transfers, including:
              </p>
              <ul className="space-y-2 ml-6">
                <li>Standard contractual clauses approved by the European Commission</li>
                <li>Adequacy decisions for countries with equivalent data protection standards</li>
                <li>Other appropriate safeguards as required by applicable law</li>
              </ul>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">10. Cookies and Tracking Technologies</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                We use cookies and similar tracking technologies to enhance your experience. For detailed information about our use of cookies, please see our <Link to="/cookies" className="text-blue-600 hover:underline">Cookie Policy</Link>.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">11. Children's Privacy</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                Our service is not intended for children under 13 years of age. We do not knowingly collect personal information from children under 13. If you are a parent or guardian and believe your child has provided us with personal information, please contact us immediately.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">12. Changes to This Policy</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                We may update this Privacy Policy from time to time to reflect changes in our practices or for other operational, legal, or regulatory reasons. We will notify you of any material changes by:
              </p>
              <ul className="space-y-2 ml-6">
                <li>Posting the updated policy on our website</li>
                <li>Sending you an email notification</li>
                <li>Displaying a notice on our platform</li>
              </ul>
              <p>
                Your continued use of our service after any changes indicates your acceptance of the updated policy.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">13. Contact Us</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                If you have any questions about this Privacy Policy or our data practices, please contact us:
              </p>
              <div className="space-y-2">
                <p><strong>Email:</strong> TODO</p>
                <p><strong>Support:</strong> TODO</p>
                <p><strong>Data Protection Officer:</strong> TODO</p>
              </div>
              <p>
                For EU residents, you also have the right to lodge a complaint with your local data protection authority.
              </p>
            </div>
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

export default PrivacyPolicy; 