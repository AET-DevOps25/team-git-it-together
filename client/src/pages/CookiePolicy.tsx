import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { Cookie, Settings, Shield, Info, Clock, Globe, Eye } from 'lucide-react';

const CookiePolicy = () => {
  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      
      <div className="mx-auto max-w-4xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <div className="flex justify-center mb-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-r from-orange-100 to-yellow-100">
              <Cookie className="h-8 w-8 text-orange-600" />
            </div>
          </div>
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Cookie Policy
          </h1>
          <p className="text-xl text-gray-600">
            Last updated: July 2025
          </p>
        </div>

        <div className="prose prose-lg max-w-none">
          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">1. Introduction</h2>
            <div className="flex items-start space-x-3 mb-4">
              <Info className="h-5 w-5 text-blue-600 mt-1" />
              <div>
                <p className="text-gray-600">
                  This Cookie Policy explains how SkillForge uses cookies and similar tracking technologies when you visit our website 
                  and use our learning platform. This policy should be read alongside our Privacy Policy, which explains how we use 
                  your personal information.
                </p>
              </div>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">2. What Are Cookies and Tracking Technologies</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                Cookies are small text files that are stored on your device (computer, tablet, or mobile) when you visit our website. 
                They help us provide you with a better experience by:
              </p>
              <ul className="space-y-2 ml-6">
                <li>• Remembering your preferences and settings</li>
                <li>• Understanding how you use our platform</li>
                <li>• Providing personalized content and recommendations</li>
                <li>• Ensuring security and preventing fraud</li>
                <li>• Improving website performance and functionality</li>
              </ul>
              <p>
                We also use similar technologies such as web beacons, pixel tags, and local storage to enhance your experience.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">3. Types of Cookies We Use</h2>
            <div className="space-y-6">
              <div className="border border-gray-200 rounded-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Essential Cookies</h3>
                <div className="flex items-start space-x-3">
                  <Shield className="h-5 w-5 text-green-600 mt-1" />
                  <div className="text-gray-600">
                    <p><strong>Purpose:</strong> These cookies are necessary for the website to function properly and cannot be switched off.</p>
                    <p className="mt-2"><strong>Examples:</strong></p>
                    <ul className="mt-2 ml-6 space-y-1">
                      <li>• Authentication and session management</li>
                      <li>• Security features and CSRF protection</li>
                      <li>• Basic functionality and navigation</li>
                      <li>• Shopping cart and checkout processes</li>
                    </ul>
                    <p className="mt-2"><strong>Legal Basis:</strong> Legitimate interest (necessary for service provision)</p>
                  </div>
                </div>
              </div>

              <div className="border border-gray-200 rounded-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Functional Cookies</h3>
                <div className="flex items-start space-x-3">
                  <Settings className="h-5 w-5 text-blue-600 mt-1" />
                  <div className="text-gray-600">
                    <p><strong>Purpose:</strong> These cookies enable enhanced functionality and personalization.</p>
                    <p className="mt-2"><strong>Examples:</strong></p>
                    <ul className="mt-2 ml-6 space-y-1">
                      <li>• Language and regional preferences</li>
                      <li>• Learning progress and course preferences</li>
                      <li>• User interface customization</li>
                      <li>• Remembering your login status</li>
                    </ul>
                    <p className="mt-2"><strong>Legal Basis:</strong> Consent or legitimate interest</p>
                  </div>
                </div>
              </div>

              <div className="border border-gray-200 rounded-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Analytics Cookies</h3>
                <div className="flex items-start space-x-3">
                  <Eye className="h-5 w-5 text-purple-600 mt-1" />
                  <div className="text-gray-600">
                    <p><strong>Purpose:</strong> These cookies help us understand how visitors interact with our platform.</p>
                    <p className="mt-2"><strong>Examples:</strong></p>
                    <ul className="mt-2 ml-6 space-y-1">
                      <li>• Page views and navigation patterns</li>
                      <li>• Feature usage and engagement metrics</li>
                      <li>• Performance monitoring and error tracking</li>
                      <li>• User journey analysis</li>
                    </ul>
                    <p className="mt-2"><strong>Legal Basis:</strong> Consent</p>
                  </div>
                </div>
              </div>

              <div className="border border-gray-200 rounded-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-3">Marketing Cookies</h3>
                <div className="flex items-start space-x-3">
                  <Cookie className="h-5 w-5 text-red-600 mt-1" />
                  <div className="text-gray-600">
                    <p><strong>Purpose:</strong> These cookies are used to deliver relevant advertisements and track marketing campaign performance.</p>
                    <p className="mt-2"><strong>Examples:</strong></p>
                    <ul className="mt-2 ml-6 space-y-1">
                      <li>• Targeted advertising and retargeting</li>
                      <li>• Social media integration and sharing</li>
                      <li>• Email marketing campaign tracking</li>
                      <li>• Conversion tracking and attribution</li>
                    </ul>
                    <p className="mt-2"><strong>Legal Basis:</strong> Consent</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">4. Third-Party Cookies and Services</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                We may use third-party services that also place cookies on your device. These services include:
              </p>
              <div className="space-y-4">
                <div className="flex items-start space-x-3">
                  <Globe className="h-5 w-5 text-blue-600 mt-1" />
                  <div>
                    <p><strong>Analytics Services:</strong> Google Analytics, Mixpanel, or similar services to understand platform usage and improve our services.</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3">
                  <Shield className="h-5 w-5 text-green-600 mt-1" />
                  <div>
                    <p><strong>Security Services:</strong> Cloudflare, reCAPTCHA, or similar services to protect against fraud and ensure security.</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3">
                  <Settings className="h-5 w-5 text-purple-600 mt-1" />
                  <div>
                    <p><strong>Content Delivery Networks:</strong> CDN services to improve loading speeds and performance.</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3">
                  <Cookie className="h-5 w-5 text-orange-600 mt-1" />
                  <div>
                    <p><strong>Social Media:</strong> Facebook, Twitter, LinkedIn, or similar platforms for sharing and integration features.</p>
                  </div>
                </div>
              </div>
              <p className="mt-4">
                These third-party services have their own privacy policies and cookie practices. We encourage you to review their policies for more information.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">5. Cookie Retention Periods</h2>
            <div className="space-y-4 text-gray-600">
              <div className="flex items-start space-x-3">
                <Clock className="h-5 w-5 text-blue-600 mt-1" />
                <div>
                  <p>Cookies are stored for different periods depending on their purpose:</p>
                </div>
              </div>
              <ul className="space-y-2 ml-6">
                <li>• <strong>Session Cookies:</strong> Deleted when you close your browser (typically 1-24 hours)</li>
                <li>• <strong>Persistent Cookies:</strong> Remain until they expire or are manually deleted (typically 30 days to 2 years)</li>
                <li>• <strong>Essential Cookies:</strong> Usually session-based or expire within 24 hours</li>
                <li>• <strong>Analytics Cookies:</strong> Typically stored for 1-2 years</li>
                <li>• <strong>Marketing Cookies:</strong> Usually stored for 30-90 days</li>
              </ul>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">6. Managing Your Cookie Preferences</h2>
            <div className="space-y-4 text-gray-600">
              <p>You can control and manage cookies in several ways:</p>
              <div className="space-y-4">
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Browser Settings</h4>
                  <p>Most browsers allow you to manage cookies through settings. You can:</p>
                  <ul className="mt-2 ml-6 space-y-1">
                    <li>• Block all cookies or specific types of cookies</li>
                    <li>• Delete existing cookies</li>
                    <li>• Set preferences for future cookies</li>
                    <li>• Enable "Do Not Track" signals</li>
                  </ul>
                </div>
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Cookie Consent Banner</h4>
                  <p>Use our cookie consent banner to manage your preferences for non-essential cookies. You can:</p>
                  <ul className="mt-2 ml-6 space-y-1">
                    <li>• Accept or reject specific cookie categories</li>
                    <li>• Change your preferences at any time</li>
                    <li>• Withdraw consent for previously accepted cookies</li>
                  </ul>
                </div>
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Third-Party Opt-outs</h4>
                  <p>Visit third-party websites to opt out of their cookies:</p>
                  <ul className="mt-2 ml-6 space-y-1">
                    <li>• Google Analytics: <a href="https://tools.google.com/dlpage/gaoptout" target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">Google Analytics Opt-out</a></li>
                    <li>• Facebook: <a href="https://www.facebook.com/help/568137493302217" target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">Facebook Cookie Settings</a></li>
                    <li>• Twitter: <a href="https://help.twitter.com/en/rules-and-policies/twitter-cookies" target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">Twitter Cookie Policy</a></li>
                  </ul>
                </div>
              </div>
              <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-yellow-800">
                  <strong>Note:</strong> Disabling certain cookies may affect the functionality of our platform. Essential cookies cannot be disabled as they are necessary for basic functionality.
                </p>
              </div>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">7. GDPR and Cookie Consent</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                Under the General Data Protection Regulation (GDPR), we are required to obtain your consent before setting non-essential cookies.
              </p>
              <ul className="space-y-2 ml-6">
                <li>• <strong>Consent:</strong> We will ask for your explicit consent before setting non-essential cookies</li>
                <li>• <strong>Granular Control:</strong> You can choose which types of cookies to accept</li>
                <li>• <strong>Withdrawal:</strong> You can withdraw your consent at any time</li>
                <li>• <strong>Transparency:</strong> We provide clear information about what each cookie does</li>
              </ul>
              <p>
                Essential cookies are set based on legitimate interest as they are necessary for the website to function.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">8. Mobile Applications</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                Our mobile applications may use similar tracking technologies, including:
              </p>
              <ul className="space-y-2 ml-6">
                <li>• Device identifiers and advertising IDs</li>
                <li>• Local storage and app preferences</li>
                <li>• Analytics and crash reporting</li>
                <li>• Push notification tokens</li>
              </ul>
              <p>
                You can manage these settings through your device's privacy settings or within the app itself.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">9. Updates to This Policy</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                We may update this Cookie Policy from time to time to reflect changes in our practices, technology, or legal requirements. 
                We will notify you of any material changes by:
              </p>
              <ul className="space-y-2 ml-6">
                <li>• Posting the updated policy on our website</li>
                <li>• Sending you an email notification</li>
                <li>• Displaying a notice on our platform</li>
                <li>• Requiring renewed consent for cookie categories that have changed</li>
              </ul>
              <p>
                Your continued use of our service after any changes indicates your acceptance of the updated policy.
              </p>
            </div>
          </section>

          <section className="mb-8">
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">10. Contact Us</h2>
            <div className="space-y-4 text-gray-600">
              <p>
                If you have any questions about our use of cookies or this Cookie Policy, please contact us:
              </p>
              <div className="space-y-2">
                <p><strong>Email:</strong> privacy@skillforge.ai</p>
                <p><strong>Support:</strong> Through our platform's help center</p>
                <p><strong>Data Protection Officer:</strong> dpo@skillforge.ai</p>
              </div>
              <p>
                For EU residents, you also have the right to lodge a complaint with your local data protection authority regarding our cookie practices.
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

export default CookiePolicy; 