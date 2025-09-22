const jwt = require("jsonwebtoken");
const User = require("../models/User"); // Import model User

const protect = async (req, res, next) => {
  let token;
  if (
    req.headers.authorization &&
    req.headers.authorization.startsWith("Bearer")
  ) {
    try {
      token = req.headers.authorization.split(" ")[1];
      const decoded = jwt.verify(token, process.env.JWT_SECRET);

      // Ganti users.find dengan User.findByPk (Find by Primary Key)
      req.user = await User.findByPk(decoded.id);

      if (!req.user) {
        return res
          .status(401)
          .json({ message: "Not authorized, user not found" });
      }
      next();
    } catch (error) {
      res.status(401).json({ message: "Not authorized, token failed" });
    }
  }
  if (!token) {
    res.status(401).json({ message: "Not authorized, no token" });
  }
};

// ... (fungsi checkRole tetap sama, tidak perlu diubah)
const checkRole = (roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      return res
        .status(403)
        .json({ message: `Forbidden: You do not have the required role.` });
    }
    next();
  };
};

module.exports = { protect, checkRole };
