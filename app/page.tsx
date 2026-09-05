"use client";
import React from "react";
import { ContainerScroll } from "@/components/ui/container-scroll-animation";
import { Rocket } from "lucide-react";
import Image from "next/image";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function Home() {
  return (
    <div className="flex flex-col overflow-hidden bg-gradient-to-br from-indigo-50 to-white dark:from-zinc-950 dark:to-zinc-900 min-h-screen">
      <nav className="flex justify-between items-center p-6 md:px-12 backdrop-blur-md bg-white/50 dark:bg-zinc-950/50 fixed w-full z-50">
        <div className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-600 to-purple-600 flex items-center gap-2">
          <Rocket className="w-6 h-6 text-indigo-500" />
          PayrollPro
        </div>
        <Link href="/login">
          <Button className="bg-indigo-600 hover:bg-indigo-700 text-white rounded-full px-8 shadow-lg hover:shadow-indigo-500/30 transition-all hover:scale-105">
            Login Portal
          </Button>
        </Link>
      </nav>

      <div className="pb-[400px] pt-[50px]">
        <ContainerScroll
          titleComponent={
            <>
              <h1 className="text-4xl md:text-[5rem] font-semibold text-black dark:text-white mt-24 mb-6 leading-none tracking-tight">
                Streamline your HR <br />
                <span className="text-5xl md:text-[6rem] font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-500 to-indigo-600 mt-2 leading-none">
                  With PayrollPro
                </span>
              </h1>
              <p className="text-neutral-500 dark:text-neutral-400 text-lg md:text-2xl max-w-2xl mx-auto mb-8 font-medium">
                A secure, Spring Boot powered Next.js application that scales natively out of the box.
              </p>
            </>
          }
        >
          <Image
            src={`https://images.unsplash.com/photo-1551288049-bebda4e38f71?q=80&w=2070&auto=format&fit=crop`}
            alt="hero"
            height={720}
            width={1400}
            className="mx-auto rounded-2xl object-cover h-full object-left-top shadow-2xl"
            draggable={false}
            priority
          />
        </ContainerScroll>
      </div>
    </div>
  );
}