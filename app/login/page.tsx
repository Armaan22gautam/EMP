"use client";

import { useState } from "react";
import { fetchApi } from "@/lib/api";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { AlertCircle, LockKeyhole, User } from "lucide-react";

export default function LoginPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [formData, setFormData] = useState({
        username: "",
        password: "",
        userType: "employee",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleRoleChange = (value: string) => {
        setFormData({ ...formData, userType: value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError("");

        try {
            const response = await fetchApi("/api/auth/login", {
                method: "POST",
                body: JSON.stringify(formData),
            });

            if (response && response.success) {
                if (response.userType === "admin") {
                    router.push("/admin-dashboard");
                } else {
                    router.push("/employee-dashboard");
                }
            } else {
                setError(response?.message || "Invalid credentials");
            }
        } catch (err: any) {
            setError(err.message || "Failed to connect to the server.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 p-4">
            <Card className="w-full max-w-md shadow-2xl bg-white/95 backdrop-blur border-none rounded-2xl overflow-hidden">
                <CardHeader className="space-y-1 pb-8 text-center bg-gradient-to-r from-blue-50 to-indigo-50 border-b">
                    <div className="flex justify-center mb-4">
                        <div className="p-3 bg-indigo-100 rounded-full">
                            <LockKeyhole className="w-8 h-8 text-indigo-600" />
                        </div>
                    </div>
                    <CardTitle className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-600 to-purple-600">
                        Welcome Back
                    </CardTitle>
                    <CardDescription className="text-gray-500 font-medium text-md">
                        Payroll Management System
                    </CardDescription>
                </CardHeader>
                <CardContent className="pt-8 px-8">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {error && (
                            <div className="p-3 bg-red-50 text-red-600 text-sm rounded-lg flex items-center gap-2 border border-red-100">
                                <AlertCircle className="w-4 h-4" />
                                <span>{error}</span>
                            </div>
                        )}

                        <div className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="username" className="text-gray-700 font-medium">Username or Name</Label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <User className="h-4 w-4 text-gray-400" />
                                    </div>
                                    <Input
                                        id="username"
                                        name="username"
                                        placeholder="Enter your username"
                                        required
                                        value={formData.username}
                                        onChange={handleChange}
                                        className="pl-10 h-12 focus-visible:ring-indigo-500"
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
                                        placeholder="Enter your password"
                                        required
                                        value={formData.password}
                                        onChange={handleChange}
                                        className="pl-10 h-12 focus-visible:ring-indigo-500"
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="userType" className="text-gray-700 font-medium">Login As</Label>
                                <Select
                                    value={formData.userType}
                                    onValueChange={handleRoleChange}
                                >
                                    <SelectTrigger className="h-12 focus:ring-indigo-500">
                                        <SelectValue placeholder="Select role" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="employee">Employee</SelectItem>
                                        <SelectItem value="admin">Administrator</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>

                        <Button
                            type="submit"
                            className="w-full h-12 text-lg font-medium bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 shadow-xl transition-all hover:scale-[1.02]"
                            disabled={loading}
                        >
                            {loading ? "Authenticating..." : "Sign In"}
                        </Button>
                    </form>
                </CardContent>
                <CardFooter className="flex justify-center border-t py-6 bg-gray-50/50">
                    <p className="text-sm text-gray-500">
                        New employee?{" "}
                        <a href="/signup" className="text-indigo-600 hover:text-indigo-800 font-medium underline underline-offset-2">
                            Sign up here
                        </a>
                    </p>
                </CardFooter>
            </Card>
        </div>
    );
}
