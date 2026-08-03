import NavbarComponent from "./NavbarComponent";
import FooterComponent from "./FooterComponent";

function Layout({ children }) {
  return (
    <>
      <NavbarComponent />

      <main className="container my-4">
        {children}
      </main>

      <FooterComponent />
    </>
  );
}

export default Layout;