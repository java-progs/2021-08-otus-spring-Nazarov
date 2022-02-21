import React, { Component } from 'react';
import { Link } from 'react-router-dom';

class Menu extends Component {
    render() {
        return (
            <div>
                <span><Link to="/books">Books</Link></span> | <span><Link to="/authors">Authors</Link></span> | <span><Link to="/genres">Genres</Link></span>
            </div>
        )
    }
}

export default Menu;