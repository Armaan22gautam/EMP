"use client";

import { useEffect, useState } from "react";
import { fetchApi, API_BASE_URL } from "@/lib/api";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";
import { BuildingIcon, DownloadIcon, LogOutIcon, PencilIcon, UserPlusIcon, Trash2Icon } from "lucide-react";

type Employee = {
    id: number;
    name: string;
    email: string;
    salary: number;
    address: string;
    createdAt: string;
};

export default function AdminDashboard() {
    const router = useRouter();
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [loading, setLoading] = useState(true);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);

    // Pagination State
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(5);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        salary: "",
        address: "",
    });

    useEffect(() => {
        loadEmployees();
    }, [page, size]); // Reload when page or size changes

    const loadEmployees = async () => {
        try {
            setLoading(true);
            const data = await fetchApi(`/api/employees?page=${page}&size=${size}`);
            if (data) {
                // Spring Boot Page<T> format
                setEmployees(data.content || []);
                setTotalPages(data.totalPages || 0);
                setTotalElements(data.totalElements || 0);
            }
        } catch (error: any) {
            toast.error(error.message || "Failed to load employees");
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

    const handleDownloadPayroll = (id: number) => {
        // Open in new tab since it returns a file attachment directly
        window.open(`${API_BASE_URL}/api/employees/${id}/payroll`, "_blank");
    };

    const handleDelete = async (id: number) => {
        if (!confirm("Are you sure you want to delete this employee?")) return;
        try {
            const response = await fetchApi(`/api/employees/${id}`, { method: "DELETE" });
            if (response?.success) {
                toast.success("Employee deleted successfully");
                loadEmployees();
            }
        } catch (error: any) {
            toast.error(error.message || "Failed to delete employee");
        }
    };

    const handleEdit = async (employee: Employee) => {
        setEditingId(employee.id);
        setFormData({
            name: employee.name,
            email: employee.email,
            password: "", // Left blank intentionally for security
            salary: employee.salary.toString(),
            address: employee.address,
        });
        setIsDialogOpen(true);
    };

    const handleOpenNewDialog = () => {
        setEditingId(null);
        setFormData({
            name: "",
            email: "",
            password: "",
            salary: "",
            address: "",
        });
        setIsDialogOpen(true);
    };

    const handleSubmitForm = async (e: React.FormEvent) => {
        e.preventDefault();
        const payload = {
            ...formData,
            salary: parseFloat(formData.salary),
        };

        try {
            let response;
            if (editingId) {
                response = await fetchApi(`/api/employees/${editingId}`, {
                    method: "PUT",
                    body: JSON.stringify(payload),
                });
            } else {
                response = await fetchApi("/api/employees/register", {
                    method: "POST",
                    body: JSON.stringify(payload),
                });
            }

            if (response?.success) {
                toast.success(editingId ? "Employee updated" : "Employee created");
                setIsDialogOpen(false);
                loadEmployees();
            } else {
                toast.error(response?.message || "Operation failed");
            }
        } catch (error: any) {
            toast.error(error.message || "Network error");
        }
    };

    return (
        <div className="min-h-screen bg-neutral-50 dark:bg-zinc-950 p-4 md:p-8">
            <div className="max-w-7xl mx-auto space-y-8">

                {/* Header */}
                <div className="flex flex-col md:flex-row justify-between items-center bg-white dark:bg-zinc-900 p-6 rounded-2xl shadow-sm border border-neutral-200 dark:border-zinc-800">
                    <div className="flex items-center gap-4 mb-4 md:mb-0">
                        <div className="p-3 bg-indigo-100 dark:bg-indigo-900/30 rounded-full">
                            <BuildingIcon className="w-6 h-6 text-indigo-600 dark:text-indigo-400" />
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-neutral-900 dark:text-white">Admin Dashboard</h1>
                            <p className="text-sm text-neutral-500">Manage your company employees and payroll details.</p>
                        </div>
                    </div>
                    <div className="flex gap-4">
                        <Button variant="outline" className="hidden sm:flex" onClick={loadEmployees}>
                            Refresh Data
                        </Button>
                        <Button variant="destructive" onClick={handleLogout} className="flex gap-2">
                            <LogOutIcon className="w-4 h-4" />
                            Logout
                        </Button>
                    </div>
                </div>

                {/* Action Bar */}
                <div className="flex justify-between items-center">
                    <h2 className="text-xl font-semibold dark:text-gray-200">Employee Directory</h2>

                    <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                        <DialogTrigger asChild>
                            <Button onClick={handleOpenNewDialog} className="bg-indigo-600 hover:bg-indigo-700 text-white flex gap-2">
                                <UserPlusIcon className="w-4 h-4" />
                                Add Employee
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle>{editingId ? "Edit Employee" : "Add New Employee"}</DialogTitle>
                            </DialogHeader>
                            <form onSubmit={handleSubmitForm} className="space-y-4 pt-4">
                                <div className="grid gap-2">
                                    <Label htmlFor="name">Full Name</Label>
                                    <Input id="name" value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })} required />
                                </div>
                                <div className="grid gap-2">
                                    <Label htmlFor="email">Email</Label>
                                    <Input id="email" type="email" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} required />
                                </div>
                                {!editingId && (
                                    <div className="grid gap-2">
                                        <Label htmlFor="password">Password</Label>
                                        <Input id="password" type="password" value={formData.password} onChange={(e) => setFormData({ ...formData, password: e.target.value })} required={!editingId} />
                                    </div>
                                )}
                                <div className="grid gap-2">
                                    <Label htmlFor="salary">Salary ($)</Label>
                                    <Input id="salary" type="number" step="0.01" value={formData.salary} onChange={(e) => setFormData({ ...formData, salary: e.target.value })} required />
                                </div>
                                <div className="grid gap-2">
                                    <Label htmlFor="address">Address</Label>
                                    <Textarea id="address" value={formData.address} onChange={(e) => setFormData({ ...formData, address: e.target.value })} required />
                                </div>
                                <Button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700">Save Employee</Button>
                            </form>
                        </DialogContent>
                    </Dialog>
                </div>

                {/* Data Table */}
                <div className="bg-white dark:bg-zinc-900 rounded-2xl border border-neutral-200 dark:border-zinc-800 shadow-sm overflow-hidden">
                    {loading ? (
                        <div className="p-12 text-center text-neutral-500">Loading directory...</div>
                    ) : (
                        <div className="overflow-x-auto">
                            <Table>
                                <TableHeader className="bg-neutral-50 dark:bg-zinc-800/50">
                                    <TableRow>
                                        <TableHead className="w-[80px]">ID</TableHead>
                                        <TableHead>Name</TableHead>
                                        <TableHead>Email</TableHead>
                                        <TableHead>Salary</TableHead>
                                        <TableHead>Address</TableHead>
                                        <TableHead>Joined</TableHead>
                                        <TableHead className="text-right">Actions</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {employees.map((employee) => (
                                        <TableRow key={employee.id} className="cursor-pointer hover:bg-neutral-50/50 dark:hover:bg-zinc-800/50">
                                            <TableCell className="font-medium">#{employee.id}</TableCell>
                                            <TableCell>{employee.name}</TableCell>
                                            <TableCell>{employee.email}</TableCell>
                                            <TableCell>${employee.salary?.toLocaleString()}</TableCell>
                                            <TableCell className="max-w-[150px] truncate" title={employee.address}>{employee.address}</TableCell>
                                            <TableCell>{new Date(employee.createdAt).toLocaleDateString()}</TableCell>
                                            <TableCell className="text-right">
                                                <div className="flex justify-end gap-2">
                                                    <Button variant="ghost" size="icon" onClick={() => handleDownloadPayroll(employee.id)} title="Download Payroll" className="text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 dark:hover:bg-emerald-900/30">
                                                        <DownloadIcon className="w-4 h-4" />
                                                    </Button>
                                                    <Button variant="ghost" size="icon" onClick={() => handleEdit(employee)} title="Edit Configuration" className="text-indigo-600 hover:text-indigo-700 hover:bg-indigo-50 dark:hover:bg-indigo-900/30">
                                                        <PencilIcon className="w-4 h-4" />
                                                    </Button>
                                                    <Button variant="ghost" size="icon" onClick={() => handleDelete(employee.id)} title="Remove Employee" className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-900/30">
                                                        <Trash2Icon className="w-4 h-4" />
                                                    </Button>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    {employees.length === 0 && (
                                        <TableRow>
                                            <TableCell colSpan={7} className="h-24 text-center text-muted-foreground">
                                                No employees found.
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </div>
                    )}

                    {/* Pagination Controls */}
                    {!loading && employees.length > 0 && (
                        <div className="flex items-center justify-between px-6 py-4 border-t border-neutral-200 dark:border-zinc-800 bg-neutral-50/50 dark:bg-zinc-900">
                            <div className="text-sm text-neutral-500">
                                Showing total {totalElements} employees (Page {page + 1} of {totalPages === 0 ? 1 : totalPages})
                            </div>
                            <div className="flex items-center gap-2">
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => setPage(p => Math.max(0, p - 1))}
                                    disabled={page === 0}
                                >
                                    Previous
                                </Button>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                    disabled={page >= totalPages - 1}
                                >
                                    Next
                                </Button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
