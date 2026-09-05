"use client";

import { useEffect, useState } from "react";
import { fetchApi, API_BASE_URL } from "@/lib/api";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { toast } from "sonner";
import { BriefcaseIcon, DownloadIcon, LogOutIcon, MapPinIcon, MailIcon, DollarSignIcon, CalendarIcon } from "lucide-react";

type Employee = {
    id: number;
    name: string;
    email: string;
    salary: number;
    address: string;
    createdAt: string;
};

export default function EmployeeDashboard() {
    const router = useRouter();
    const [employee, setEmployee] = useState<Employee | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        try {
            setLoading(true);
            const session = await fetchApi("/api/auth/session");
            if (session?.authenticated && session.user?.id) {
                const data = await fetchApi(`/api/employees/${session.user.id}`);
                if (data) setEmployee(data);
            } else {
                router.push("/login");
            }
        } catch (error: any) {
            toast.error(error.message || "Failed to load profile");
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        try {
            await fetchApi("/api/auth/logout", { method: "POST" });
            router.push("/login");
            toast.success("Successfully logged out");
        } catch (error) {
            router.push("/login");
        }
    };

    const handleDownloadPayroll = () => {
        if (employee?.id) {
            window.open(`${API_BASE_URL}/api/employees/${employee.id}/payroll`, "_blank");
        }
    };

    if (loading) {
        return <div className="min-h-screen flex items-center justify-center text-neutral-500">Loading your profile...</div>;
    }

    if (!employee) {
        return <div className="min-h-screen flex items-center justify-center text-red-500">Failed to load employee profile data.</div>;
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-purple-50 dark:from-zinc-950 dark:to-zinc-900 p-4 md:p-8">
            <div className="max-w-4xl mx-auto space-y-8">

                {/* Header */}
                <div className="flex flex-col md:flex-row justify-between items-center bg-white/60 dark:bg-zinc-900/60 backdrop-blur-lg p-6 rounded-2xl shadow-sm border border-neutral-200 dark:border-zinc-800">
                    <div className="flex items-center gap-4 mb-4 md:mb-0">
                        <div className="p-3 bg-gradient-to-br from-indigo-500 to-purple-500 rounded-2xl shadow-lg">
                            <BriefcaseIcon className="w-8 h-8 text-white" />
                        </div>
                        <div>
                            <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-600 to-purple-600">
                                Welcome, {employee.name.split(' ')[0]}!
                            </h1>
                            <p className="text-sm text-neutral-500 font-medium">Employee Portal</p>
                        </div>
                    </div>
                    <div className="flex gap-4">
                        <Button variant="outline" className="hidden sm:flex border-indigo-200 text-indigo-700 hover:bg-indigo-50" onClick={loadProfile}>
                            Refresh Status
                        </Button>
                        <Button variant="ghost" onClick={handleLogout} className="flex gap-2 text-neutral-600 hover:text-red-600 hover:bg-red-50">
                            <LogOutIcon className="w-4 h-4" />
                            Sign Out
                        </Button>
                    </div>
                </div>

                {/* Profile Card */}
                <Card className="border-none shadow-xl bg-white/80 dark:bg-zinc-900/80 backdrop-blur-xl rounded-3xl overflow-hidden">
                    <CardHeader className="bg-neutral-50/50 dark:bg-zinc-800/30 border-b px-8 py-6">
                        <CardTitle className="text-2xl font-semibold">Your Personal Profile</CardTitle>
                        <CardDescription className="text-base">Review your securely encrypted employment details below.</CardDescription>
                    </CardHeader>
                    <CardContent className="p-8 grid gap-8 md:grid-cols-2">

                        <div className="space-y-6">
                            <div className="flex items-center gap-4">
                                <div className="p-3 bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 rounded-xl">
                                    <MailIcon className="w-6 h-6" />
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-neutral-500">Business Email</p>
                                    <p className="text-lg font-semibold dark:text-white">{employee.email}</p>
                                </div>
                            </div>

                            <div className="flex items-center gap-4">
                                <div className="p-3 bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400 rounded-xl">
                                    <DollarSignIcon className="w-6 h-6" />
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-neutral-500">Annual Salary Base</p>
                                    <p className="text-lg font-semibold dark:text-white">${employee.salary?.toLocaleString()}</p>
                                </div>
                            </div>
                        </div>

                        <div className="space-y-6">
                            <div className="flex items-center gap-4">
                                <div className="p-3 bg-orange-100 dark:bg-orange-900/30 text-orange-600 dark:text-orange-400 rounded-xl">
                                    <MapPinIcon className="w-6 h-6" />
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-neutral-500">Registered Address</p>
                                    <p className="text-lg font-semibold dark:text-white truncate max-w-[200px]" title={employee.address}>{employee.address}</p>
                                </div>
                            </div>

                            <div className="flex items-center gap-4">
                                <div className="p-3 bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 rounded-xl">
                                    <CalendarIcon className="w-6 h-6" />
                                </div>
                                <div>
                                    <p className="text-sm font-medium text-neutral-500">Join Date</p>
                                    <p className="text-lg font-semibold dark:text-white">{new Date(employee.createdAt).toLocaleDateString()}</p>
                                </div>
                            </div>
                        </div>

                    </CardContent>
                </Card>

                {/* Action Widgets */}
                <div className="grid gap-6 md:grid-cols-2">
                    <Card className="border-none shadow-lg bg-gradient-to-br from-emerald-500 to-teal-600 text-white rounded-3xl cursor-pointer hover:shadow-xl transition-all hover:scale-[1.02]" onClick={handleDownloadPayroll}>
                        <CardContent className="p-8 flex items-center justify-between">
                            <div>
                                <h3 className="text-2xl font-bold mb-2">My Payroll</h3>
                                <p className="text-emerald-100">Download your latest direct deposit and tax breakdown safely.</p>
                            </div>
                            <div className="p-4 bg-white/20 rounded-full backdrop-blur-md">
                                <DownloadIcon className="w-8 h-8" />
                            </div>
                        </CardContent>
                    </Card>
                </div>

            </div>
        </div>
    );
}
