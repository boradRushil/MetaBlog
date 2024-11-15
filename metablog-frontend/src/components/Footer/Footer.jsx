import { Link } from 'react-router-dom';

const Footer = () => {
    return (
        <footer className="p-4 bg-gray-100">
            <div className="grid gap-4 md:grid-cols-4">
                <div>
                    <h4 className="font-bold">About</h4>
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
                    <p>Email: info@metablog.net</p>
                    <p>Phone: 800-123-456-789</p>
                </div>
                <div>
                    <h4 className="font-bold">Quick Links</h4>
                    <ul>
                        <li><Link to="/">Home</Link></li>
                        <li><Link to="/about">About</Link></li>
                        <li><Link to="/blog">Blog</Link></li>
                        <li><Link to="/archived">Archived</Link></li>
                        <li><Link to="/author">Author</Link></li>
                        <li><Link to="/contact">Contact</Link></li>
                    </ul>
                </div>
                <div>
                    <h4 className="font-bold">Category</h4>
                    <ul>
                        <li><Link to="/category/data-science">Data Science</Link></li>
                        <li><Link to="/category/ai">Artificial Intelligence</Link></li>
                        <li><Link to="/category/blockchain">Blockchain</Link></li>
                        <li><Link to="/category/software-development">Software Development</Link></li>
                        <li><Link to="/category/cloud-computing">Cloud Computing</Link></li>
                        <li><Link to="/category/security">Security</Link></li>
                    </ul>
                </div>
            </div>
            <div className="flex justify-between mt-4">
                <span>&copy; 2024 MetaBlog</span>
                <div className="flex space-x-4">
                    <Link to="/terms">Terms of Use</Link>
                    <Link to="/privacy">Privacy Policy</Link>
                    <Link to="/cookies">Cookie Policy</Link>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
