"use client";

import { useState } from "react";
import { fetchApi } from "@/lib/api";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { AlertCircle, CheckCircle2, UserPlus, User, Mail, LockKeyhole, DollarSign, MapPin } from "lucide-react";

export default function SignupPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        salary: "",
        address: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        setSuccess("");

        try {
            const payload = {
                ...formData,
                salary: parseFloat(formData.salary),
            };

            const response = await fetchApi("/api/employees/register", {
                method: "POST",
                body: JSON.stringify(payload),
            });

            if (response?.success) {
                setSuccess("Registration successful! Redirecting to login...");
                setFormData({ name: "", email: "", password: "", salary: "", address: "" });
                setTimeout(() => {
                    router.push("/login");
                }, 2000);
            } else {
                setError(response?.message || "Registration failed");
            }
        } catch (err: any) {
            setError(err.message || "Failed to connect to the server.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-emerald-500 via-teal-500 to-cyan-500 p-4">
            <Card className="w-full max-w-md shadow-2xl bg-white/95 backdrop-blur border-none rounded-2xl overflow-hidden">
                <CardHeader className="space-y-1 pb-6 text-center bg-gradient-to-r from-emerald-50 to-teal-50 border-b">
                    <div className="flex justify-center mb-4">
                        <div className="p-3 bg-emerald-100 rounded-full">
                            <UserPlus className="w-8 h-8 text-emerald-600" />
                        </div>
                    </div>
                    <CardTitle className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-emerald-600 to-teal-600">
                        Create Account
                    </CardTitle>
                    <CardDescription className="text-gray-500 font-medium text-md">
                        Employee Registration Portal
                    </CardDescription>
                </CardHeader>
                <CardContent className="pt-6 px-8">
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {error && (
                            <div className="p-3 bg-red-50 text-red-600 text-sm rounded-lg flex items-center gap-2 border border-red-100">
                                <AlertCircle className="w-4 h-4 flex-shrink-0" />
                                <span>{error}</span>
                            </div>
                        )}

                        {success && (
                            <div className="p-3 bg-emerald-50 text-emerald-600 text-sm rounded-lg flex items-center gap-2 border border-emerald-100">
                                <CheckCircle2 className="w-4 h-4 flex-shrink-0" />
                                <span>{success}</span>
                            </div>
                        )}

                        <div className="space-y-2">
                            <Label htmlFor="name" className="text-gray-700 font-medium">Full Name</Label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <User className="h-4 w-4 text-gray-400" />
                                </div>
                                <Input
                                    id="name"
                                    name="name"
                                    placeholder="Enter your full name"
                                    required
                                    value={formData.name}
                                    onChange={handleChange}
                                    className="pl-10 h-11 focus-visible:ring-emerald-500"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email" className="text-gray-700 font-medium">Email</Label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-4 w-4 text-gray-400" />
                                </div>
                                <Input
                                    id="email"
                                    name="email"
                                    type="email"
                                    placeholder="Enter your email"
                                    required
                                    value={formData.email}
                                    onChange={handleChange}
                                    className="pl-10 h-11 focus-visible:ring-emerald-500"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password" className="text-gray-700 font-medium">Password</Label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <LockKeyhole className="h-4 w-4 text-gray-400" />
                                </div>
                                <Input
                                    id="password"
                                    name="password"
                                    type="password"
                                    placeholder="Create a password"
                                    required
                                    value={formData.password}
                                    onChange={handleChange}
                                    className="pl-10 h-11 focus-visible:ring-emerald-500"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="salary" className="text-gray-700 font-medium">Annual Salary ($)</Label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <DollarSign className="h-4 w-4 text-gray-400" />
                                </div>
                                <Input
                                    id="salary"
                                    name="salary"
                                    type="number"
                                    step="0.01"
                                    placeholder="Enter annual salary"
                                    required
                                    value={formData.salary}
                                    onChange={handleChange}
                                    className="pl-10 h-11 focus-visible:ring-emerald-500"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="address" className="text-gray-700 font-medium">Address</Label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 pt-2.5 pointer-events-none">
                                    <MapPin className="h-4 w-4 text-gray-400" />
                                </div>
                                <Textarea
                                    id="address"
                                    name="address"
                                    placeholder="Enter your address"
                                    required
                                    value={formData.address}
                                    onChange={handleChange}
                                    className="pl-10 min-h-[80px] focus-visible:ring-emerald-500"
                                />
                            </div>
                        </div>

                        <Button
                            type="submit"
                            className="w-full h-12 text-lg font-medium bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 shadow-xl transition-all hover:scale-[1.02]"
                            disabled={loading}
                        >
                            {loading ? "Creating Account..." : "Register"}
                        </Button>
                    </form>
                </CardContent>
                <CardFooter className="flex justify-center border-t py-6 bg-gray-50/50">
                    <p className="text-sm text-gray-500">
                        Already have an account?{" "}
                        <a href="/login" className="text-emerald-600 hover:text-emerald-800 font-medium underline underline-offset-2">
                            Sign in here
                        </a>
                    </p>
                </CardFooter>
            </Card>
        </div>
    );
}
