import { useState } from "react";
import Header from "./header/header";
import Sidebar from "./sidebar/sidebar";

interface Props {
    children: React.ReactNode;
    title: string;
}

const DashboardDefaults: React.FC<Props> = ({ children, title }) => {
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    return (
        <>
            <section className="dashboard-layout">
                <section className="container-responsive">
                    <Sidebar />
                    <div className="main-content">
                        {<Header onMenuToggle={() => setMobileMenuOpen(!mobileMenuOpen)} title={title} />}

                        <main className="content">{children}</main>
                    </div>
                </section>
            </section>
        </>
    );
};

export default DashboardDefaults;
