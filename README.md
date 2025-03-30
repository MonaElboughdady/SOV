# SOV
This project implements an orbital visualization system using **NASA WorldWind** for 3D rendering and **Patrius** for orbital mechanics. It allows users to dynamically propagate orbits and visualize the trajectory of a satellite in real-time.

## Features  
- Converts **Patrius Geodetic Points** into **WorldWind Positions** for accurate rendering.  
- Dynamically updates the satellite's orbit based on **Keplerian orbital elements**.  
- Propagates the orbit and computes the **trajectory**.  
- Displays both the **orbit path** and a **3D satellite representation**.  
- Interactive **sliders** to adjust orbital parameters in real-time.  
- Uses **Runge-Kutta integration** for numerical propagation.  
